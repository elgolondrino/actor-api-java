package im.actor.torlib.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.errors.TorException;
import im.actor.torlib.proxy.actors.ProxyAcceptActor;

public class SocksTorProxy {
    private final static Logger logger = Logger.getLogger(SocksTorProxy.class.getName());
    private final Set<Integer> listeningPorts = new HashSet<Integer>();
    private final Map<Integer, AcceptTask> acceptThreads = new HashMap<Integer, AcceptTask>();
    private final CircuitManager circuitManager;
    private final ExecutorService executor;
    private boolean isStopped;

    public SocksTorProxy(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
        this.executor = Executors.newCachedThreadPool();
    }

    public void addListeningPort(int port) {
        if (port <= 0 || port > 65535) {
            throw new TorException("Illegal listening port: " + port);
        }

        synchronized (listeningPorts) {
            if (isStopped) {
                throw new IllegalStateException("Cannot add listening port because Socks proxy has been stopped");
            }
            if (listeningPorts.contains(port))
                return;
            listeningPorts.add(port);
            try {
                startListening(port);
                logger.fine("Listening for SOCKS connections on port " + port);
            } catch (IOException e) {
                listeningPorts.remove(port);
                throw new TorException("Failed to listen on port " + port + " : " + e.getMessage());
            }
        }

    }

    public void stop() {
        synchronized (listeningPorts) {
            for (AcceptTask t : acceptThreads.values()) {
                t.stop();
            }
            executor.shutdownNow();
            isStopped = true;
        }
    }

    private void startListening(int port) throws IOException {
        final AcceptTask task = new AcceptTask(port);
        acceptThreads.put(port, task);
        executor.execute(task);
    }

    private class AcceptTask implements Runnable {
        private final ServerSocket socket;
        private final int port;
        private volatile boolean stopped;

        public AcceptTask(int port) throws IOException {
            this.socket = new ServerSocket(port);
            this.port = port;
        }

        public void run() {
            try {
                while (!Thread.interrupted() && !stopped) {
                    ProxyAcceptActor.acceptConnection(socket.accept(), circuitManager);
                }
            } catch (IOException e) {
                if (!stopped) {
                    logger.warning("System error accepting SOCKS socket connections: " + e.getMessage());
                }
            } finally {
                synchronized (listeningPorts) {
                    listeningPorts.remove(port);
                    acceptThreads.remove(port);
                }
            }
        }

        public void stop() {
            stopped = true;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
