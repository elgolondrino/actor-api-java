package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.InternalCircuit;
import im.actor.torlib.circuits.path.PathSelectionFailedException;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class InternalCircuitFactory extends CircuitFactory<InternalCircuit> {
    private CircuitManager circuitManager;

    public InternalCircuitFactory(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public List<Router> buildNewPath() {
        try {
            return circuitManager.getPathChooser().chooseInternalPath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PathSelectionFailedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InternalCircuit buildNewCircuit(List<Router> path, Connection connection) {
        return new InternalCircuit(path, connection, circuitManager);
    }
}
