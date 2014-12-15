package im.actor.torlib.directory;

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

public class DirectoryManager {
    private final static Logger logger = Logger.getLogger(DirectoryManager.class.getName());

    private CircuitManager circuitManager;
    private boolean isStarted;
    private boolean isStopped;

    private ConsensusSyncInt directorySync;
    private DescriptorsSyncInt descriptorsSync;

    public DirectoryManager(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    public void start(NewDirectory directory) {
        if (isStarted) {
            logger.warning("Directory manager already running");
            return;
        }

        directorySync = ConsensusSyncActor.get(directory, circuitManager);
        directorySync.startSync();

        descriptorsSync = DescriptorsSyncActor.get(directory, circuitManager);
        descriptorsSync.startSync();

        isStarted = true;
    }

    public void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        isStopped = true;
        isStarted = false;
        directorySync.stopSync();
        descriptorsSync.stopSync();
    }
}
