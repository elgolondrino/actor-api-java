package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.InternalCircuitImpl;
import im.actor.torlib.circuits.path.PathSelectionFailedException;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class InternalCircuitFactory extends CircuitFactory<InternalCircuitImpl> {
    private CircuitManager circuitManager;

    public InternalCircuitFactory(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public InternalCircuitImpl buildNewCircuit() {
        try {
            return new InternalCircuitImpl(circuitManager.getPathChooser().chooseInternalPath(), circuitManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
