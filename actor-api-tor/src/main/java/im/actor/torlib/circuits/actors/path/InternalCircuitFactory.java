package im.actor.torlib.circuits.actors.path;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.connections.ConnectionImpl;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class InternalCircuitFactory extends CircuitFactory {
    private CircuitManager circuitManager;

    public InternalCircuitFactory(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public List<Router> buildNewPath() {
        try {
            return circuitManager.getPathChooser().chooseInternalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Circuit buildNewCircuit(List<Router> path, ConnectionImpl connection) {
        return new Circuit(path, connection, circuitManager);
    }
}
