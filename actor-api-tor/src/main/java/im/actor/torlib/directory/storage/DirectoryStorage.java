package im.actor.torlib.directory.storage;

import im.actor.torlib.Document;
import im.actor.torlib.TorConfig;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryStorage {
    public enum CacheFile {
        CERTIFICATES("certificates"),
        CONSENSUS("consensus"),
        CONSENSUS_MICRODESC("consensus-microdesc"),
        MICRODESCRIPTOR_CACHE("cached-microdescs"),
        MICRODESCRIPTOR_JOURNAL("cached-microdescs.new"),
        DESCRIPTOR_CACHE("cached-descriptors"),
        DESCRIPTOR_JOURNAL("cached-descriptors.new"),
        STATE("state");

        final private String filename;

        CacheFile(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    private final TorConfig config;
    private Map<CacheFile, DirectoryStorageFile> fileMap;

    public DirectoryStorage(TorConfig config) {
        this.config = config;
        this.fileMap = new HashMap<CacheFile, DirectoryStorageFile>();
    }

    public synchronized ByteBuffer loadCacheFile(CacheFile cacheFile) {
        return getStoreFile(cacheFile).loadContents();
    }

    public synchronized void writeData(CacheFile cacheFile, ByteBuffer data) {
        getStoreFile(cacheFile).writeData(data);
    }

    public synchronized void writeDocument(CacheFile cacheFile, Document document) {
        writeDocumentList(cacheFile, Arrays.asList(document));
    }

    public synchronized void writeDocumentList(CacheFile cacheFile, List<? extends Document> documents) {
        getStoreFile(cacheFile).writeDocuments(documents);
    }

    public synchronized void appendDocumentList(CacheFile cacheFile, List<? extends Document> documents) {
        getStoreFile(cacheFile).appendDocuments(documents);
    }

    public synchronized void removeCacheFile(CacheFile cacheFile) {
        getStoreFile(cacheFile).remove();
    }

    public synchronized void removeAllCacheFiles() {
        for (CacheFile cf : CacheFile.values()) {
            getStoreFile(cf).remove();
        }
    }

    private DirectoryStorageFile getStoreFile(CacheFile cacheFile) {
        if (!fileMap.containsKey(cacheFile)) {
            fileMap.put(cacheFile, new DirectoryStorageFile(config, cacheFile.getFilename()));
        }
        return fileMap.get(cacheFile);
    }
}