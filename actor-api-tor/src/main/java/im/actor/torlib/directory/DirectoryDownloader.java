package im.actor.torlib.directory;

import java.util.Arrays;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.directory.sync.ConsensusSyncActor;
import im.actor.torlib.directory.sync.ConsensusSyncInt;
import im.actor.torlib.directory.sync.DescriptorsSyncActor;
import im.actor.torlib.directory.sync.DescriptorsSyncInt;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.documents.downloader.DirectoryDocumentRequestor;
import im.actor.torlib.errors.DirectoryRequestFailedException;
import im.actor.torlib.documents.DescriptorDocument;

public class DirectoryDownloader {
    private final static Logger logger = Logger.getLogger(DirectoryDownloader.class.getName());

    private CircuitManager circuitManager;
    private boolean isStarted;
    private boolean isStopped;

    private ConsensusSyncInt directorySync;
    private DescriptorsSyncInt descriptorsSync;

    public DirectoryDownloader(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    public synchronized void start(NewDirectory directory) {
        if (isStarted) {
            logger.warning("Directory downloader already running");
            return;
        }

        directorySync = ConsensusSyncActor.get(directory, circuitManager);
        directorySync.startSync();

        descriptorsSync = DescriptorsSyncActor.get(directory, circuitManager);
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

    private DirectoryCircuit openBridgeCircuit(Router bridge) throws DirectoryRequestFailedException {
        try {
            return circuitManager.openDirectoryCircuitTo(Arrays.asList(bridge));
        } catch (OpenFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory circuit to bridge " + bridge, e);
        }
    }
}
