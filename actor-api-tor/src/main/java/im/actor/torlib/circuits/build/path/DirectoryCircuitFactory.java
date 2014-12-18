package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class DirectoryCircuitFactory extends CircuitFactory<DirectoryCircuit> {

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
    public DirectoryCircuit buildNewCircuit(List<Router> path, Connection connection) {
        return new DirectoryCircuit(path, connection, circuitManager);
    }
}
