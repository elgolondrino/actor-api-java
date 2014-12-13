package im.actor.torlib.directory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.directory.sync.ConsensusSyncActor;
import im.actor.torlib.directory.sync.ConsensusSyncInt;
import im.actor.torlib.directory.sync.DescriptorsSyncActor;
import im.actor.torlib.directory.sync.DescriptorsSyncInt;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.state.TorInitializationTracker;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.downloader.DirectoryDocumentRequestor;
import im.actor.torlib.errors.DirectoryRequestFailedException;
import im.actor.torlib.documents.DescriptorDocument;

public class DirectoryDownloader {
    private final static Logger logger = Logger.getLogger(DirectoryDownloader.class.getName());

    private final TorInitializationTracker initializationTracker;
    private CircuitManager circuitManager;
    private boolean isStarted;
    private boolean isStopped;

    private ConsensusSyncInt directorySync;
    private DescriptorsSyncInt descriptorsSync;

    public DirectoryDownloader(TorInitializationTracker initializationTracker, CircuitManager circuitManager) {
        this.initializationTracker = initializationTracker;
        this.circuitManager = circuitManager;
    }

    public synchronized void start(NewDirectory directory) {
        if (isStarted) {
            logger.warning("Directory downloader already running");
            return;
        }

        directorySync = ConsensusSyncActor.get(directory, circuitManager);
        directorySync.startSync();

        descriptorsSync = DescriptorsSyncActor.get(directory, this);
        descriptorsSync.startSync();

        isStarted = true;
    }

    public synchronized void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        isStopped = true;
        isStarted = false;
        directorySync.stopSync();
        descriptorsSync.stopSync();
    }

    public DescriptorDocument downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openBridgeCircuit(bridge));
        return requestor.downloadBridgeDescriptor();
    }

    public List<DescriptorDocument> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException {
        return downloadRouterDescriptors(fingerprints, openCircuit());
    }

    public List<DescriptorDocument> downloadRouterDescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(circuit, initializationTracker);
        final List<DescriptorDocument> ds = requestor.downloadRouterDescriptors(fingerprints);
        return removeUnrequestedDescriptors(fingerprints, ds);
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
            logger.warning("Discarding " + unrequestedCount + " received descriptor(s) with fingerprints that did not match requested descriptors");
        }
        return result;
    }

    private DirectoryCircuit openCircuit() throws DirectoryRequestFailedException {
        try {
            return circuitManager.openDirectoryCircuit();
        } catch (OpenFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory circuit", e);
        }
    }

    private DirectoryCircuit openBridgeCircuit(Router bridge) throws DirectoryRequestFailedException {
        try {
            return circuitManager.openDirectoryCircuitTo(Arrays.asList(bridge));
        } catch (OpenFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory circuit to bridge " + bridge, e);
        }
    }
}
