package im.actor.torlib;

import java.util.logging.Level;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.crypto.PRNGFixes;
import im.actor.torlib.directory.DirectoryManager;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.socks.SocksTorProxy;
import im.actor.torlib.utils.Tor;

/**
 * This class is the main entry-point for running a Tor proxy
 * or client.
 */
public class TorClient {
    private final static Logger logger = Logger.getLogger(TorClient.class.getName());
    private final TorConfig config;
    private final NewDirectory newDirectory;
    private final ConnectionCache connectionCache;
    private final CircuitManager circuitManager;
    private final SocksTorProxy socksListener;
    private final DirectoryManager directoryManager;

    private boolean isStarted = false;
    private boolean isStopped = false;

    public TorClient() {
        if (Tor.isAndroidRuntime()) {
            PRNGFixes.apply();
        }
        config = Tor.createConfig();
        newDirectory = new NewDirectory(config);

        connectionCache = new ConnectionCache(config);

        circuitManager = new CircuitManager(config, newDirectory, connectionCache);
        directoryManager = new DirectoryManager(circuitManager);

        socksListener = new SocksTorProxy(circuitManager);
    }

    public TorConfig getConfig() {
        return config;
    }

    /**
     * Start running the Tor client service.
     */
    public synchronized void start() {
        if (isStarted) {
            return;
        }
        if (isStopped) {
            throw new IllegalStateException("Cannot restart a TorClient instance.  Create a new instance instead.");
        }
        logger.info("Starting AcTOR (version: " + Tor.getFullVersion() + ")");
        directoryManager.start(newDirectory);
        circuitManager.startBuildingCircuits();
        isStarted = true;
    }

    public synchronized void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        try {
            socksListener.stop();
            directoryManager.stop();
            circuitManager.stopBuildingCircuits();
            newDirectory.close();
            connectionCache.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception while shutting down TorClient instance: " + e, e);
        } finally {
            isStopped = true;
        }
    }

    @Deprecated
    public void enableSocksListener(int port) {
        socksListener.addListeningPort(port);
    }

    @Deprecated
    public void enableSocksListener() {
        enableSocksListener(9150);
    }
}
