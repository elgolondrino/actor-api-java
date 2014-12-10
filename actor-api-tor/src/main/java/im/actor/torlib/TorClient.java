package im.actor.torlib;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

import im.actor.torlib.circuits.TorInitializationTracker;
import im.actor.torlib.crypto.PRNGFixes;
import im.actor.torlib.dashboard.Dashboard;
import im.actor.torlib.directory.Directory;
import im.actor.torlib.sockets.OrchidSocketFactory;

/**
 * This class is the main entry-point for running a Tor proxy
 * or client.
 */
public class TorClient {
    private final static Logger logger = Logger.getLogger(TorClient.class.getName());
    private final TorConfig config;
    private final Directory directory;
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
        this(null);
    }

    public TorClient(DirectoryStore customDirectoryStore) {
        if (Tor.isAndroidRuntime()) {
            PRNGFixes.apply();
        }
        config = Tor.createConfig();
        directory = new Directory(config, customDirectoryStore);
        initializationTracker = Tor.createInitalizationTracker();
        initializationTracker.addListener(createReadyFlagInitializationListener());
        connectionCache = Tor.createConnectionCache(config, initializationTracker);
        directoryDownloader = new DirectoryDownloader(config, initializationTracker);
        circuitManager = new CircuitManager(config, directoryDownloader, directory, connectionCache, initializationTracker);
        socksListener = Tor.createSocksPortListener(config, circuitManager);
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
        // verifyUnlimitedStrengthPolicyInstalled();
        directoryDownloader.start(directory);
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
            directory.close();
            connectionCache.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception while shutting down TorClient instance: " + e, e);
        } finally {
            isStopped = true;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public ConnectionCache getConnectionCache() {
        return connectionCache;
    }

    public CircuitManager getCircuitManager() {
        return circuitManager;
    }

    public void waitUntilReady() throws InterruptedException {
        readyLatch.await();
    }

    public void waitUntilReady(long timeout) throws InterruptedException, TimeoutException {
        if (!readyLatch.await(timeout, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
    }

    public Stream openExitStreamTo(String hostname, int port) throws InterruptedException, TimeoutException, OpenFailedException {
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

    private static TorInitializationListener createInitalizationListner() {
        return new TorInitializationListener() {

            public void initializationProgress(String message, int percent) {
                System.out.println(">>> [ " + percent + "% ]: " + message);
            }

            public void initializationCompleted() {
                System.out.println("Tor is ready to go!");
            }
        };
    }
}
