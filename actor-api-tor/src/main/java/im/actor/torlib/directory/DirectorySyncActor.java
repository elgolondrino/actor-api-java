package im.actor.torlib.directory;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import im.actor.torlib.KeyCertificate;
import im.actor.utils.Threading;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.Timestamp;
import im.actor.torlib.directory.downloader.DescriptorProcessor;
import im.actor.torlib.directory.downloader.DirectoryRequestFailedException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class DirectorySyncActor extends TypedActor<DirectorySyncInt> implements DirectorySyncInt {

    public static DirectorySyncInt get(final Directory directory, final DirectoryDownloader directoryDownloader) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(DirectorySyncActor.class, new ActorCreator<DirectorySyncActor>() {
            @Override
            public DirectorySyncActor create() {
                return new DirectorySyncActor(directory, directoryDownloader);
            }
        }), "directories/" + directory.getId() + "/sync"), DirectorySyncInt.class);
    }

    private final static Logger logger = Logger.getLogger(DirectorySyncActor.class.getName());

    private final ExecutorService executor = Threading.newPool("DirectoryDownloadTask worker");

    private final TorRandom random = new TorRandom();

    private Directory directory;

    private ConsensusDocument currentConsensus;
    private Date consensusDownloadTime;

    private volatile boolean isDownloadingCertificates;
    private volatile boolean isDownloadingConsensus;

    private final AtomicInteger outstandingDescriptorTasks = new AtomicInteger();

    private final DescriptorProcessor descriptorProcessor;

    private final DirectoryDownloader downloader;

    public DirectorySyncActor(Directory directory, DirectoryDownloader directoryDownloader) {
        super(DirectorySyncInt.class);
        this.directory = directory;
        this.downloader = directoryDownloader;
        this.descriptorProcessor = new DescriptorProcessor(directory);
    }

    @Override
    public void startDirectorySync() {
        directory.loadFromStore();
        setCurrentConsensus(directory.getCurrentConsensusDocument());

        while (true) {
            checkCertificates();
            checkConsensus();
            checkDescriptors();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public void stopSync() {
        context().stopSelf();
    }

    private void checkCertificates() {
        if (isDownloadingCertificates
                || directory.getRequiredCertificates().isEmpty()) {
            return;
        }

        isDownloadingCertificates = true;
        executor.execute(new DownloadCertificatesTask());
    }

    private void checkConsensus() {
        if (isDownloadingConsensus || !needConsensusDownload()) {
            return;
        }

        isDownloadingConsensus = true;
        executor.execute(new DownloadConsensusTask());
    }

    private void checkDescriptors() {
        if (outstandingDescriptorTasks.get() > 0) {
            return;
        }
        List<List<HexDigest>> ds = descriptorProcessor.getDescriptorDigestsToDownload();
        if (ds.isEmpty()) {
            return;
        }
        for (List<HexDigest> dlist : ds) {
            outstandingDescriptorTasks.incrementAndGet();
            executor.execute(new DownloadRouterDescriptorsTask(dlist));
        }
    }

    private boolean needConsensusDownload() {
        if (directory.hasPendingConsensus()) {
            return false;
        }
        if (currentConsensus == null || !currentConsensus.isLive()) {
            if (currentConsensus == null) {
                logger.info("Downloading consensus because we have no consensus document");
            } else {
                logger.info("Downloading consensus because the document we have is not live");
            }
            return true;
        }
        return consensusDownloadTime.before(new Date());
    }

    void setCurrentConsensus(ConsensusDocument consensus) {
        if (consensus != null) {
            currentConsensus = consensus;
            consensusDownloadTime = chooseDownloadTimeForConsensus(consensus);
        } else {
            currentConsensus = null;
            consensusDownloadTime = null;
        }
    }

    /*
     * dir-spec 5.1: Downloading network-status documents
	 *
	 *   To avoid swarming the caches whenever a consensus expires, the clients
	 *   download new consensuses at a randomly chosen time after the caches are
	 *   expected to have a fresh consensus, but before their consensus will
	 *   expire. (This time is chosen uniformly at random from the interval
	 *   between the time 3/4 into the first interval after the consensus is no
	 *   longer fresh, and 7/8 of the time remaining after that before the
	 *   consensus is invalid.)
	 *
	 *   [For example, if a cache has a consensus that became valid at 1:00, and
	 *   is fresh until 2:00, and expires at 4:00, that cache will fetch a new
	 *   consensus at a random time between 2:45 and 3:50, since 3/4 of the
	 *   one-hour interval is 45 minutes, and 7/8 of the remaining 75 minutes is
	 *   65 minutes.]
	 */
    private Date chooseDownloadTimeForConsensus(ConsensusDocument consensus) {
        final long va = getMilliseconds(consensus.getValidAfterTime());
        final long fu = getMilliseconds(consensus.getFreshUntilTime());
        final long vu = getMilliseconds(consensus.getValidUntilTime());
        final long i1 = fu - va;
        final long start = fu + ((i1 * 3) / 4);
        final long i2 = ((vu - start) * 7) / 8;
        final long r = random.nextLong(i2);
        final long download = start + r;
        return new Date(download);
    }

    private static long getMilliseconds(Timestamp ts) {
        return ts.getDate().getTime();
    }


    private class DownloadConsensusTask implements Runnable {
        public void run() {
            try {
                final ConsensusDocument consensus = downloader.downloadCurrentConsensus();
                setCurrentConsensus(consensus);
                directory.addConsensusDocument(consensus, false);

            } catch (DirectoryRequestFailedException e) {
                logger.warning("Failed to download current consensus document: " + e.getMessage());
            } finally {
                isDownloadingConsensus = false;
            }
        }
    }

    private class DownloadRouterDescriptorsTask implements Runnable {
        private final Set<HexDigest> fingerprints;

        public DownloadRouterDescriptorsTask(Collection<HexDigest> fingerprints) {
            this.fingerprints = new HashSet<HexDigest>(fingerprints);
        }

        public void run() {
            try {
                directory.addRouterMicrodescriptors(downloader.downloadRouterMicrodescriptors(fingerprints));
            } catch (DirectoryRequestFailedException e) {
                logger.warning("Failed to download router descriptors: " + e.getMessage());
            } finally {
                outstandingDescriptorTasks.decrementAndGet();
            }
        }
    }

    private class DownloadCertificatesTask implements Runnable {
        public void run() {
            try {
                for (KeyCertificate c : downloader.downloadKeyCertificates(directory.getRequiredCertificates())) {
                    directory.addCertificate(c);
                }
                directory.storeCertificates();
            } catch (DirectoryRequestFailedException e) {
                logger.warning("Failed to download key certificates: " + e.getMessage());
            } finally {
                isDownloadingCertificates = false;
            }
        }
    }
}
