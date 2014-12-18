package im.actor.torlib.circuits.actors.path;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.connections.ConnectionImpl;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class DirectoryCircuitFactory extends CircuitFactory {

    private CircuitManager circuitManager;

    public DirectoryCircuitFactory(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public List<Router> buildNewPath() {
        try {
            return circuitManager.getPathChooser().chooseDirectoryPath();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Circuit buildNewCircuit(List<Router> path, ConnectionImpl connection) {
        return new Circuit(path, connection, circuitManager);
    }
}
