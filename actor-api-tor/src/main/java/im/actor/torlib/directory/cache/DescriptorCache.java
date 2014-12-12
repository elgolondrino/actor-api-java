package im.actor.torlib.directory.cache;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.directory.storage.DirectoryStorage.CacheFile;
import im.actor.utils.Threading;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.utils.misc.GuardedBy;

public abstract class DescriptorCache <T extends DescriptorDocument> {
	private final static Logger logger = Logger.getLogger(DescriptorCache.class.getName());
	
	private final DescriptorCacheData<T> data;

	private final DirectoryStorage store;
	private final ScheduledExecutorService rebuildExecutor =
			Threading.newScheduledPool("DescriptorCache rebuild worker");

	private final CacheFile cacheFile;
	private final CacheFile journalFile;
	
	@GuardedBy("this")
	private int droppedBytes;
	
	@GuardedBy("this")
	private int journalLength;
	
	@GuardedBy("this")
	private int cacheLength;
	
	@GuardedBy("this")
	private boolean initiallyLoaded;

	public DescriptorCache(DirectoryStorage store, CacheFile cacheFile, CacheFile journalFile) {
		this.data = new DescriptorCacheData<T>();
		this.store = store;
		this.cacheFile = cacheFile;
		this.journalFile = journalFile;
		startRebuildTask();
	}

	public synchronized void initialLoad() {
		if(initiallyLoaded) {
			return;
		}
		reloadCache();
	}
	
	public void shutdown() {
		rebuildExecutor.shutdownNow();
	}

	public T getDescriptor(HexDigest digest) {
		return data.findByDigest(digest);
	}

	public synchronized void addDescriptors(List<T> descriptors) {
		final List<T> journalDescriptors = new ArrayList<T>();
		int duplicateCount = 0;
		for(T d: descriptors) {
			if(data.addDescriptor(d)) {
				if(d.getCacheLocation() == DescriptorDocument.CacheLocation.NOT_CACHED) {
					journalLength += d.getBodyLength();
					journalDescriptors.add(d);
				}
			} else {
				duplicateCount += 1;
			}
		}

		if(!journalDescriptors.isEmpty()) {
			store.appendDocumentList(journalFile, journalDescriptors);
		}
		if(duplicateCount > 0) {
			logger.info("Duplicate descriptors added to journal, count = "+ duplicateCount);
		}
	}

	public void addDescriptor(T d) {
		final List<T> descriptors = new ArrayList<T>();
		descriptors.add(d);
		addDescriptors(descriptors);
	}
	
	private synchronized void clearMemoryCache() {
		data.clear();
		journalLength = 0;
		cacheLength = 0;
		droppedBytes = 0;
	}

	private synchronized void reloadCache() {
		clearMemoryCache();
		final ByteBuffer[] buffers = loadCacheBuffers();
		loadCacheFileBuffer(buffers[0]);
		loadJournalFileBuffer(buffers[1]);
		if(!initiallyLoaded) {
			initiallyLoaded = true;
		}
	}

	private ByteBuffer[] loadCacheBuffers() {
		synchronized (store) {
			final ByteBuffer[] buffers = new ByteBuffer[2];
			buffers[0] = store.loadCacheFile(cacheFile);
			buffers[1] = store.loadCacheFile(journalFile);
			return buffers;
		}
	}

	private void loadCacheFileBuffer(ByteBuffer buffer) {
		cacheLength = buffer.limit();
		if(cacheLength == 0) {
			return;
		}
		final DocumentParser<T> parser = createDocumentParser(buffer);
		final DocumentParsingResult<T> result = parser.parse();
		if(result.isOkay()) {
			for(T d: result.getParsedDocuments()) {
				d.setCacheLocation(DescriptorDocument.CacheLocation.CACHED_CACHEFILE);
				data.addDescriptor(d);
			}
		}

	}
	
	private void loadJournalFileBuffer(ByteBuffer buffer) {
		journalLength = buffer.limit();
		if(journalLength == 0) {
			return;
		}
		final DocumentParser<T> parser = createDocumentParser(buffer);
		final DocumentParsingResult<T> result = parser.parse();
		if(result.isOkay()) {
			int duplicateCount = 0;
			logger.fine("Loaded "+ result.getParsedDocuments().size() + " descriptors from journal");
			for(T d: result.getParsedDocuments()) {
				d.setCacheLocation(DescriptorDocument.CacheLocation.CACHED_JOURNAL);
				if(!data.addDescriptor(d)) {
					duplicateCount += 1;
				}
			} 
			if(duplicateCount > 0) {
				logger.info("Found "+ duplicateCount + " duplicate descriptors in journal file");
			}
		} else if(result.isInvalid()) {
			logger.warning("Invalid descriptor data parsing from journal file : "+ result.getMessage());
		} else if(result.isError()) {
			logger.warning("Error parsing descriptors from journal file : "+ result.getMessage());			
		}
	}
	
	abstract protected DocumentParser<T> createDocumentParser(ByteBuffer buffer);
	
	private ScheduledFuture<?> startRebuildTask() {
		return rebuildExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				maybeRebuildCache();
			}
		}, 5, 30, TimeUnit.MINUTES);
	}
	
	private synchronized void maybeRebuildCache() {
		if(!initiallyLoaded) {
			return;
		}
		
		droppedBytes += data.cleanExpired();
		
		if(!shouldRebuildCache()) {
			return;
		}
		rebuildCache();
	}
	
	private boolean shouldRebuildCache() {
		if(journalLength < 16384) {
			return false;
		}
		if(droppedBytes > (journalLength + cacheLength) / 3) {
			return true;
		}
		if(journalLength > (cacheLength / 2)) {
			return true;
		}
		return false;
	}
	
	private void rebuildCache() {
		synchronized(store) {
			store.writeDocumentList(cacheFile, data.getAllDescriptors());
			store.removeCacheFile(journalFile);
		}
		reloadCache();
	}
}
