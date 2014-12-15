package im.actor.torlib.circuits.build;

import java.util.logging.Level;
import java.util.logging.Logger;

import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitNodeImpl;
import im.actor.torlib.circuits.build.extender.CircuitExtender;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.ConnectionFailedException;
import im.actor.torlib.errors.ConnectionHandshakeException;
import im.actor.torlib.errors.ConnectionTimeoutException;
import im.actor.torlib.errors.TorException;

public class CircuitBuildTask implements Runnable {
    private final static Logger logger = Logger.getLogger(CircuitBuildTask.class.getName());
    private final CircuitCreationRequest creationRequest;
    private final ConnectionCache connectionCache;

    private Connection connection = null;

    public CircuitBuildTask(CircuitCreationRequest request, ConnectionCache connectionCache) {
        this.creationRequest = request;
        this.connectionCache = connectionCache;
    }

    public CircuitCreationRequest getCreationRequest() {
        return creationRequest;
    }

    public void run() {
        Router firstRouter = null;
        CircuitImpl circuit = null;
        try {
            creationRequest.buildCircuit();
            circuit = creationRequest.getCircuit();
            if (circuit == null) {
                connectionFailed("Unable to create internal circuit", null);
                return;
            }
            CircuitExtender extender = new CircuitExtender(circuit);
            circuit.notifyCircuitBuildStart();

            firstRouter = circuit.getPath().get(0);

            connection = connectionCache.getConnectionTo(firstRouter);
            circuit.bindToConnection(connection);
            creationRequest.connectionCompleted(connection);


            final CircuitNodeImpl firstNode = extender.createFastTo(firstRouter);
            creationRequest.nodeAdded(firstNode);

            boolean isFirst = true;
            for (Router p : circuit.getPath()) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                final CircuitNodeImpl extendedNode = extender.extendTo(p);
                creationRequest.nodeAdded(extendedNode);
            }

            creationRequest.circuitBuildCompleted(circuit);

            circuit.notifyCircuitBuildCompleted();
        } catch (ConnectionTimeoutException e) {
            connectionFailed("Timeout connecting to " + firstRouter, circuit);
        } catch (ConnectionFailedException e) {
            connectionFailed("Connection failed to " + firstRouter + " : " + e.getMessage(), circuit);
        } catch (ConnectionHandshakeException e) {
            connectionFailed("Handshake error connecting to " + firstRouter + " : " + e.getMessage(), circuit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            circuitBuildFailed("Circuit building thread interrupted", circuit);
        } catch (TorException e) {
            circuitBuildFailed(e.getMessage(), circuit);
        } catch (Exception e) {
            circuitBuildFailed("Unexpected exception: " + e, circuit);
            logger.log(Level.WARNING, "Unexpected exception while building circuit: " + e, e);
        }
    }

    private void connectionFailed(String message, CircuitImpl circuit) {
        creationRequest.connectionFailed(message);
        if (circuit != null) {
            circuit.notifyCircuitBuildFailed();
        }
    }

    private void circuitBuildFailed(String message, CircuitImpl circuit) {
        creationRequest.circuitBuildFailed(message);
        if (circuit != null) {
            circuit.notifyCircuitBuildFailed();
            if (connection != null) {
                connection.removeCircuit(circuit);
            }
        }
    }
}
