package im.actor.torlib.circuits.build;

import java.util.List;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitBuildHandler;
import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitNode;
import im.actor.torlib.circuits.build.path.CircuitFactory;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

public class CircuitCreationRequest implements CircuitBuildHandler {

    private final CircuitFactory circuitFactory;
    private final CircuitBuildHandler buildHandler;

    private CircuitImpl circuit;

    public CircuitCreationRequest(CircuitFactory circuitFactory, CircuitBuildHandler buildHandler) {
        this.circuitFactory = circuitFactory;
        this.buildHandler = buildHandler;
    }

    public void buildCircuit() {
        circuit = circuitFactory.buildNewCircuit();
    }

    public CircuitImpl getCircuit() {
        return circuit;
    }

    public void connectionCompleted(Connection connection) {
        if (buildHandler != null) {
            buildHandler.connectionCompleted(connection);
        }
    }

    public void connectionFailed(String reason) {
        if (buildHandler != null) {
            buildHandler.connectionFailed(reason);
        }
    }

    public void nodeAdded(CircuitNode node) {
        if (buildHandler != null) {
            buildHandler.nodeAdded(node);
        }
    }

    public void circuitBuildCompleted(Circuit circuit) {
        if (buildHandler != null) {
            buildHandler.circuitBuildCompleted(circuit);
        }
    }

    public void circuitBuildFailed(String reason) {
        if (buildHandler != null) {
            buildHandler.circuitBuildFailed(reason);
        }
    }
}
