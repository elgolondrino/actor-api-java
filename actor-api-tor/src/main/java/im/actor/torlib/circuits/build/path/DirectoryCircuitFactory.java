package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuitImpl;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class DirectoryCircuitFactory extends CircuitFactory<DirectoryCircuitImpl> {

    private CircuitManager circuitManager;

    public DirectoryCircuitFactory(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    public DirectoryCircuitFactory(Router router, CircuitManager circuitManager) {

    }

    @Override
    public DirectoryCircuitImpl buildNewCircuit() {
        try {
            return new DirectoryCircuitImpl(circuitManager.getPathChooser().chooseDirectoryPath(),
                    circuitManager);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
