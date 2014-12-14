package im.actor.torlib.directory.sync;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.utils.HexDigest;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.downloader.DescriptorProcessor;
import im.actor.torlib.documents.downloader.DirectoryDocumentRequestor;
import im.actor.torlib.errors.DirectoryRequestFailedException;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.Threading;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class DescriptorsSyncActor extends TypedActor<DescriptorsSyncInt> implements DescriptorsSyncInt {

    public static DescriptorsSyncInt get(final NewDirectory directory, final CircuitManager circuitManager) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(DescriptorsSyncActor.class, new ActorCreator<DescriptorsSyncActor>() {
            @Override
            public DescriptorsSyncActor create() {
                return new DescriptorsSyncActor(directory, circuitManager);
            }
        }), "directories/" + directory.getId() + "/sync/descriptors"), DescriptorsSyncInt.class);
    }

    private final static Logger LOG = Logger.getLogger(DescriptorsSyncActor.class.getName());

    private final ExecutorService executor = Threading.newPool("DirectoryDownloadTask worker");

    private final AtomicInteger outstandingDescriptorTasks = new AtomicInteger();

    private final DescriptorProcessor descriptorProcessor;
    private final NewDirectory directory;
    private final CircuitManager circuitManager;

    public DescriptorsSyncActor(NewDirectory directory, CircuitManager circuitManager) {
        super(DescriptorsSyncInt.class);
        this.directory = directory;
        this.descriptorProcessor = new DescriptorProcessor(directory);
        this.circuitManager = circuitManager;
    }

    @Override
    public void startSync() {
        self().sendOnce(new CheckDescriptors());
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof CheckDescriptors) {
            checkDescriptors();
            self().sendOnce(new CheckDescriptors(), 5000);
        }
    }

    @Override
    public void stopSync() {
        context().stopSelf();
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

    private DirectoryCircuit openCircuit() throws DirectoryRequestFailedException {
        try {
            return circuitManager.openDirectoryCircuit();
        } catch (OpenFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory circuit", e);
        }
    }

    private class DownloadRouterDescriptorsTask implements Runnable {
        private final Set<HexDigest> fingerprints;

        public DownloadRouterDescriptorsTask(Collection<HexDigest> fingerprints) {
            this.fingerprints = new HashSet<HexDigest>(fingerprints);
        }

        public void run() {
            try {
                final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openCircuit());
                final List<DescriptorDocument> ds = requestor.downloadRouterDescriptors(fingerprints);
                directory.applyRouterDescriptors(removeUnrequestedDescriptors(fingerprints, ds));
            } catch (DirectoryRequestFailedException e) {
                LOG.warning("Failed to download router descriptors: " + e.getMessage());
            } finally {
                outstandingDescriptorTasks.decrementAndGet();
            }
        }

        private <T extends DescriptorDocument> List<T> removeUnrequestedDescriptors(Set<HexDigest> requested, List<T> received) {
            final List<T> result = new ArrayList<T>();
            int unrequestedCount = 0;
            for (T d : received) {
                if (requested.contains(d.getDescriptorDigest())) {
                    result.add(d);
                } else {
                    unrequestedCount += 1;
                }
            }
            if (unrequestedCount > 0) {
                // logger.warning("Discarding " + unrequestedCount + " received descriptor(s) with fingerprints that did not match requested descriptors");
            }
            return result;
        }
    }

    private static class CheckDescriptors {
    }
}
