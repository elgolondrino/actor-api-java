package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.ExitCircuit;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.circuits.build.ExitTarget;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class ExitCircuitFactory extends CircuitFactory<ExitCircuit> {

    private CircuitManager circuitManager;
    private List<ExitTarget> exitTargets;

    public ExitCircuitFactory(List<ExitTarget> exitTargets, CircuitManager circuitManager) {
        this.exitTargets = exitTargets;
        this.circuitManager = circuitManager;
    }

    @Override
    public List<Router> buildNewPath() {
        final Router exitRouter = circuitManager.getPathChooser().chooseExitNodeForTargets(exitTargets);
        if (exitRouter == null) {
            return null;
        }
        try {
            return circuitManager.getPathChooser().choosePathWithExit(exitRouter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ExitCircuit buildNewCircuit(List<Router> path, Connection connection) {
        return new ExitCircuit(path, connection, circuitManager);
    }
}
