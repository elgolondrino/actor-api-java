package im.actor.torlib.circuits.build;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitNodeImpl;
import im.actor.torlib.circuits.build.path.CircuitFactory;
import im.actor.torlib.connections.Connection;

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

    public void nodeAdded(CircuitNodeImpl node) {
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
