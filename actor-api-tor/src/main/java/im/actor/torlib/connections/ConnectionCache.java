package im.actor.torlib.connections;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import im.actor.torlib.errors.ConnectionFailedException;
import im.actor.torlib.errors.ConnectionHandshakeException;
import im.actor.torlib.errors.ConnectionTimeoutException;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.dashboard.DashboardRenderable;
import im.actor.torlib.dashboard.DashboardRenderer;

public class ConnectionCache implements DashboardRenderable {
    private final static Logger logger = Logger.getLogger(ConnectionCache.class.getName());

    private class ConnectionTask implements Callable<ConnectionImpl> {

        private final Router router;

        ConnectionTask(Router router) {
            this.router = router;
        }

        public ConnectionImpl call() throws Exception {
            final SSLSocket socket = factory.createSocket();
            final ConnectionImpl conn = new ConnectionImpl(config, socket, router);
            conn.connect();
            return conn;
        }
    }

    private class CloseIdleConnectionCheckTask implements Runnable {
        public void run() {
            for (Future<ConnectionImpl> f : activeConnections.values()) {
                if (f.isDone()) {
                    try {
                        final ConnectionImpl c = f.get();
                        c.idleCloseCheck();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private final ConcurrentMap<Router, Future<ConnectionImpl>> activeConnections = new ConcurrentHashMap<Router, Future<ConnectionImpl>>();
    private final ConnectionSocketFactory factory = new ConnectionSocketFactory();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private final TorConfig config;
    private volatile boolean isClosed;


    public ConnectionCache(TorConfig config) {
        this.config = config;
        scheduledExecutor.scheduleAtFixedRate(new CloseIdleConnectionCheckTask(), 5000, 5000, TimeUnit.MILLISECONDS);
    }

    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        for (Future<ConnectionImpl> f : activeConnections.values()) {
            if (f.isDone()) {
                try {
                    ConnectionImpl conn = f.get();
                    conn.closeSocket();
                } catch (InterruptedException e) {
                    logger.warning("Unexpected interruption while closing connection");
                } catch (ExecutionException e) {
                    logger.warning("Exception closing connection: " + e.getCause());
                }
            } else {
                // FIXME this doesn't close the socket, so the connection task lingers
                // A proper fix would require maintaining pending connections in a separate
                // collection.
                f.cancel(true);
            }
        }
        activeConnections.clear();
        scheduledExecutor.shutdownNow();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Connection getConnectionTo(Router router) throws InterruptedException, ConnectionTimeoutException, ConnectionFailedException, ConnectionHandshakeException {
        if (isClosed) {
            throw new IllegalStateException("ConnectionCache has been closed");
        }
        logger.fine("Get connection to " + router.getAddress() + " " + router.getOnionPort() + " " + router.getNickname());
        while (true) {
            Future<ConnectionImpl> f = getFutureFor(router);
            try {
                Connection c = f.get();
                if (c.isClosed()) {
                    activeConnections.remove(router, f);
                } else {
                    return c;
                }
            } catch (CancellationException e) {
                activeConnections.remove(router, f);
            } catch (ExecutionException e) {
                activeConnections.remove(router, f);
                final Throwable t = e.getCause();
                if (t instanceof ConnectionTimeoutException) {
                    throw (ConnectionTimeoutException) t;
                } else if (t instanceof ConnectionFailedException) {
                    throw (ConnectionFailedException) t;
                } else if (t instanceof ConnectionHandshakeException) {
                    throw (ConnectionHandshakeException) t;
                }
                throw new RuntimeException("Unexpected exception: " + e, e);
            }
        }
    }

    private Future<ConnectionImpl> getFutureFor(Router router) {
        Future<ConnectionImpl> f = activeConnections.get(router);
        if (f != null) {
            return f;
        }
        return createFutureForIfAbsent(router);
    }

    private Future<ConnectionImpl> createFutureForIfAbsent(Router router) {
        final Callable<ConnectionImpl> task = new ConnectionTask(router);
        final FutureTask<ConnectionImpl> futureTask = new FutureTask<ConnectionImpl>(task);

        final Future<ConnectionImpl> f = activeConnections.putIfAbsent(router, futureTask);
        if (f != null) {
            return f;
        }

        futureTask.run();
        return futureTask;
    }

    public void dashboardRender(DashboardRenderer renderer, PrintWriter writer, int flags) throws IOException {
        if ((flags & DASHBOARD_CONNECTIONS) == 0) {
            return;
        }
        printDashboardBanner(writer, flags);
        for (Connection c : getActiveConnections()) {
            if (!c.isClosed()) {
                renderer.renderComponent(writer, flags, c);
            }
        }
        writer.println();
    }

    private void printDashboardBanner(PrintWriter writer, int flags) {
        final boolean verbose = (flags & DASHBOARD_CONNECTIONS_VERBOSE) != 0;
        if (verbose) {
            writer.println("[Connection Cache (verbose)]");
        } else {
            writer.println("[Connection Cache]");
        }
        writer.println();
    }

    List<Connection> getActiveConnections() {
        final List<Connection> cs = new ArrayList<Connection>();
        for (Future<ConnectionImpl> future : activeConnections.values()) {
            addConnectionFromFuture(future, cs);
        }
        return cs;
    }

    private void addConnectionFromFuture(Future<ConnectionImpl> future, List<Connection> connectionList) {
        try {
            if (future.isDone() && !future.isCancelled()) {
                connectionList.add(future.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
        }
    }
}
