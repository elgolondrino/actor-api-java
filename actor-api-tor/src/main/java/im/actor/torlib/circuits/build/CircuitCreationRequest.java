package im.actor.torlib.circuits.build;

import java.util.Collections;
import java.util.List;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitBuildHandler;
import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitNode;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.circuits.path.PathSelectionFailedException;

public class CircuitCreationRequest implements CircuitBuildHandler {
    private final CircuitImpl circuit;
    private final CircuitPathChooser pathChooser;
    private final CircuitBuildHandler buildHandler;

    private List<Router> path;

    public CircuitCreationRequest(CircuitPathChooser pathChooser, Circuit circuit, CircuitBuildHandler buildHandler) {
        this.pathChooser = pathChooser;
        this.circuit = (CircuitImpl) circuit;
        this.buildHandler = buildHandler;
        this.path = Collections.emptyList();
    }

    void choosePath() throws InterruptedException, PathSelectionFailedException {
        path = circuit.choosePath(pathChooser);

    }

    CircuitImpl getCircuit() {
        return circuit;
    }

    List<Router> getPath() {
        return path;
    }

    int getPathLength() {
        return path.size();
    }

    Router getPathElement(int idx) {
        return path.get(idx);
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
