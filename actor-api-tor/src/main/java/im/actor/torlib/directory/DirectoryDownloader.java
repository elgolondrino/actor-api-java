package im.actor.torlib.directory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import im.actor.torlib.*;
import im.actor.torlib.directory.ConsensusDocument.RequiredCertificate;
import im.actor.torlib.circuits.TorInitializationTracker;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.downloader.DirectoryDocumentRequestor;
import im.actor.torlib.directory.downloader.DirectoryRequestFailedException;

public class DirectoryDownloader {
    private final static Logger logger = Logger.getLogger(DirectoryDownloader.class.getName());

    private final TorInitializationTracker initializationTracker;
    private CircuitManager circuitManager;
    private boolean isStarted;
    private boolean isStopped;
    private DirectorySyncInt directorySync;

    public DirectoryDownloader(TorInitializationTracker initializationTracker) {
        this.initializationTracker = initializationTracker;
    }

    public void setCircuitManager(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    public synchronized void start(Directory directory) {
        if (isStarted) {
            logger.warning("Directory downloader already running");
            return;
        }
        if (circuitManager == null) {
            throw new IllegalStateException("Must set CircuitManager instance with setCircuitManager() before starting.");
        }

        directorySync = DirectorySyncActor.get(directory, this);
        directorySync.startDirectorySync();
        isStarted = true;
    }

    public synchronized void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        isStopped = true;
        isStarted = false;
        directorySync.stopSync();
    }

    public Descriptor downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openBridgeCircuit(bridge));
        return requestor.downloadBridgeDescriptor(bridge);
    }

    public ConsensusDocument downloadCurrentConsensus() throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openCircuit(), initializationTracker);
        return requestor.downloadCurrentConsensus();
    }

    public List<KeyCertificate> downloadKeyCertificates(Set<RequiredCertificate> required) throws DirectoryRequestFailedException {
        return downloadKeyCertificates(required, openCircuit());
    }

    public List<KeyCertificate> downloadKeyCertificates(Set<RequiredCertificate> required, DirectoryCircuit circuit) throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(circuit, initializationTracker);
        return requestor.downloadKeyCertificates(required);
    }

    public List<Descriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException {
        return downloadRouterMicrodescriptors(fingerprints, openCircuit());
    }

    public List<Descriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException {
        final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(circuit, initializationTracker);
        final List<Descriptor> ds = requestor.downloadRouterMicrodescriptors(fingerprints);
        return removeUnrequestedDescriptors(fingerprints, ds);
    }

    private <T extends Descriptor> List<T> removeUnrequestedDescriptors(Set<HexDigest> requested, List<T> received) {
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
