package im.actor.torlib;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.TorStream;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.connections.ConnectionCacheImpl;
import im.actor.torlib.crypto.PRNGFixes;
import im.actor.torlib.dashboard.Dashboard;
import im.actor.torlib.directory.DirectoryDownloader;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.sockets.OrchidSocketFactory;
import im.actor.torlib.socks.SocksPortListener;
import im.actor.torlib.state.TorInitializationListener;
import im.actor.torlib.state.TorInitializationTracker;
import im.actor.torlib.utils.Tor;

/**
 * This class is the main entry-point for running a Tor proxy
 * or client.
 */
public class TorClient {
    private final static Logger logger = Logger.getLogger(TorClient.class.getName());
    private final TorConfig config;
    private final NewDirectory newDirectory;
    private final TorInitializationTracker initializationTracker;
    private final ConnectionCache connectionCache;
    private final CircuitManager circuitManager;
    private final SocksPortListener socksListener;
    private final DirectoryDownloader directoryDownloader;
    private final Dashboard dashboard;

    private boolean isStarted = false;
    private boolean isStopped = false;

    private final CountDownLatch readyLatch;

    public TorClient() {
        if (Tor.isAndroidRuntime()) {
            PRNGFixes.apply();
        }
        config = Tor.createConfig();
        newDirectory = new NewDirectory(config);

        initializationTracker = new TorInitializationTracker();
        initializationTracker.addListener(createReadyFlagInitializationListener());
        connectionCache = new ConnectionCacheImpl(config, initializationTracker);

        circuitManager = new CircuitManager(config, newDirectory, connectionCache, initializationTracker);
        directoryDownloader = new DirectoryDownloader(circuitManager);

        socksListener = new SocksPortListener(config, circuitManager);
        readyLatch = new CountDownLatch(1);
        dashboard = new Dashboard();
        dashboard.addRenderables(circuitManager, directoryDownloader, socksListener);
    }

    public TorConfig getConfig() {
        return config;
    }

    public SocketFactory getSocketFactory() {
        return new OrchidSocketFactory(this);
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
        logger.info("Starting Orchid (version: " + Tor.getFullVersion() + ")");
        directoryDownloader.start(newDirectory);
        circuitManager.startBuildingCircuits();
        if (dashboard.isEnabledByProperty()) {
            dashboard.startListening();
        }
        isStarted = true;
    }

    public synchronized void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        try {
            socksListener.stop();
            if (dashboard.isListening()) {
                dashboard.stopListening();
            }
            directoryDownloader.stop();
            circuitManager.stopBuildingCircuits(true);
            newDirectory.close();
            connectionCache.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception while shutting down TorClient instance: " + e, e);
        } finally {
            isStopped = true;
        }
    }

    public ConnectionCache getConnectionCache() {
        return connectionCache;
    }

    public CircuitManager getCircuitManager() {
        return circuitManager;
    }

    public TorStream openExitStreamTo(String hostname, int port) throws InterruptedException, TimeoutException, OpenFailedException {
        ensureStarted();
        return circuitManager.openExitStreamTo(hostname, port);
    }

    private synchronized void ensureStarted() {
        if (!isStarted) {
            throw new IllegalStateException("Must call start() first");
        }
    }

    public void enableSocksListener(int port) {
        socksListener.addListeningPort(port);
    }

    public void enableSocksListener() {
        enableSocksListener(9150);
    }

    public void enableDashboard() {
        if (!dashboard.isListening()) {
            dashboard.startListening();
        }
    }

    public void enableDashboard(int port) {
        dashboard.setListeningPort(port);
        enableDashboard();
    }

    public void disableDashboard() {
        if (dashboard.isListening()) {
            dashboard.stopListening();
        }
    }

    public void addInitializationListener(TorInitializationListener listener) {
        initializationTracker.addListener(listener);
    }

    public void removeInitializationListener(TorInitializationListener listener) {
        initializationTracker.removeListener(listener);
    }

    private TorInitializationListener createReadyFlagInitializationListener() {
        return new TorInitializationListener() {
            public void initializationProgress(String message, int percent) {
            }

            public void initializationCompleted() {
                readyLatch.countDown();
            }
        };
    }
}
