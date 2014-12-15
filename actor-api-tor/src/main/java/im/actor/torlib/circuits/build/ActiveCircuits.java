package im.actor.torlib.circuits.build;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitImpl;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.ExitCircuit;
import im.actor.torlib.crypto.TorRandom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class ActiveCircuits {

    public interface CircuitFilter {
        boolean filter(Circuit circuit);
    }

    private final TorRandom random = new TorRandom();

    private final Set<CircuitImpl> activeCircuits;

    public ActiveCircuits() {
        this.activeCircuits = new HashSet<CircuitImpl>();
    }

    public void addActiveCircuit(CircuitImpl circuit) {
        synchronized (activeCircuits) {
            activeCircuits.add(circuit);
            activeCircuits.notifyAll();
        }
    }

    public void removeActiveCircuit(CircuitImpl circuit) {
        synchronized (activeCircuits) {
            activeCircuits.remove(circuit);
        }
    }

    public int getActiveCircuitCount() {
        synchronized (activeCircuits) {
            return activeCircuits.size();
        }
    }

    public Set<Circuit> getPendingCircuits() {
        return getCircuitsByFilter(new CircuitFilter() {
            public boolean filter(Circuit circuit) {
                return circuit.isPending();
            }
        });
    }

    public int getPendingCircuitCount() {
        return getPendingCircuits().size();
    }

    public Set<Circuit> getCircuitsByFilter(CircuitFilter filter) {
        final Set<Circuit> result = new HashSet<Circuit>();
        final Set<CircuitImpl> circuits = new HashSet<CircuitImpl>();

        synchronized (activeCircuits) {
            // the filter might lock additional objects, causing a deadlock, so don't
            // call it inside the monitor
            circuits.addAll(activeCircuits);
        }

        for (CircuitImpl c : circuits) {
            if (filter == null || filter.filter(c)) {
                result.add(c);
            }
        }
        return result;
    }

    public List<ExitCircuit> getRandomlyOrderedListOfExitCircuits() {
        final Set<Circuit> notDirectory = getCircuitsByFilter(new CircuitFilter() {

            public boolean filter(Circuit circuit) {
                final boolean exitType = circuit instanceof ExitCircuit;
                return exitType && !circuit.isMarkedForClose() && circuit.isConnected();
            }
        });
        final ArrayList<ExitCircuit> ac = new ArrayList<ExitCircuit>();
        for (Circuit c : notDirectory) {
            if (c instanceof ExitCircuit) {
                ac.add((ExitCircuit) c);
            }
        }
        final int sz = ac.size();
        for (int i = 0; i < sz; i++) {
            final ExitCircuit tmp = ac.get(i);
            final int swapIdx = random.nextInt(sz);
            ac.set(i, ac.get(swapIdx));
            ac.set(swapIdx, tmp);
        }
        return ac;
    }

    public void close() {
        ArrayList<CircuitImpl> circuits;
        synchronized (activeCircuits) {
            circuits = new ArrayList<CircuitImpl>(activeCircuits);
        }
        for (CircuitImpl c : circuits) {
            c.destroyCircuit();
        }
    }
}
