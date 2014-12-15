package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.ExitCircuitImpl;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.directory.routers.exitpolicy.ExitTarget;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class ExitCircuitFactory extends CircuitFactory<ExitCircuitImpl> {

    private CircuitManager circuitManager;
    private List<ExitTarget> exitTargets;

    public ExitCircuitFactory(List<ExitTarget> exitTargets, CircuitManager circuitManager) {
        this.exitTargets = exitTargets;
        this.circuitManager = circuitManager;
    }

    @Override
    public ExitCircuitImpl buildNewCircuit() {
        final Router exitRouter = circuitManager.getPathChooser().chooseExitNodeForTargets(exitTargets);
        if (exitRouter == null) {
            return null;
        }
        try {
            List<Router> routers = circuitManager.getPathChooser().choosePathWithExit(exitRouter);
            return new ExitCircuitImpl(routers, circuitManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
