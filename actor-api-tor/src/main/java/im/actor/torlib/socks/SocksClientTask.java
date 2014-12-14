package im.actor.torlib.socks;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.circuits.TorStream;
import im.actor.torlib.socks.utils.ProxyConnectionActor;

public class SocksClientTask implements Runnable {
    private final static Logger LOG = Logger.getLogger(SocksClientTask.class.getName());

    private final Socket socket;

    private final CircuitManager circuitManager;

    public SocksClientTask(Socket socket, CircuitManager circuitManager) {
        this.socket = socket;
        this.circuitManager = circuitManager;
    }

    public void run() {
        final int version = readByte();
        if (!dispatchRequest(version)) {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.warning("Error closing SOCKS socket: " + e.getMessage());
            }
        }
    }

    private int readByte() {
        try {
            return socket.getInputStream().read();
        } catch (IOException e) {
            LOG.warning("IO error reading version byte: " + e.getMessage());
            return -1;
        }
    }

    private boolean dispatchRequest(int versionByte) {
        switch (versionByte) {
            case 4:
                return processRequest(new Socks4Request(socket));
            case 5:
                return processRequest(new Socks5Request(socket));
            default:
                // fall through, do nothing
                // Closing connection
                return false;
        }
    }

    private boolean processRequest(SocksRequest request) {
        try {
            request.readRequest();
            if (!request.isConnectRequest()) {
                LOG.warning("Non connect command (" + request.getCommandCode() + ")");
                request.sendError(true);
                return false;
            }

            try {
                final TorStream torStream = openConnectStream(request);
                LOG.fine("SOCKS CONNECT to " + request.getTarget() + " completed");
                request.sendSuccess();
                ProxyConnectionActor.runConnection(socket, torStream);
                return true;
            } catch (InterruptedException e) {
                LOG.info("SOCKS CONNECT to " + request.getTarget() + " was thread interrupted");
                Thread.currentThread().interrupt();
                request.sendError(false);
            } catch (TimeoutException e) {
                LOG.info("SOCKS CONNECT to " + request.getTarget() + " timed out");
                request.sendError(false);
            } catch (OpenFailedException e) {
                LOG.info("SOCKS CONNECT to " + request.getTarget() + " failed: " + e.getMessage());
                request.sendConnectionRefused();
            }
        } catch (SocksRequestException e) {
            LOG.log(Level.WARNING, "Failure reading SOCKS request: " + e.getMessage());
            try {
                request.sendError(false);
                socket.close();
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    private TorStream openConnectStream(SocksRequest request) throws InterruptedException, TimeoutException, OpenFailedException {
        if (request.hasHostname()) {
            LOG.fine("SOCKS CONNECT request to " + request.getHostname() + ":" + request.getPort());
            return circuitManager.openExitStreamTo(request.getHostname(), request.getPort());
        } else {
            LOG.fine("SOCKS CONNECT request to " + request.getAddress() + ":" + request.getPort());
            return circuitManager.openExitStreamTo(request.getAddress(), request.getPort());
        }
    }
}
