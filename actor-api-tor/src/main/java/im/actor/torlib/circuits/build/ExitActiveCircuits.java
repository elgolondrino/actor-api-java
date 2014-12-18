package im.actor.torlib.circuits.build;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.crypto.TorRandom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class ExitActiveCircuits {

    public interface CircuitFilter {
        boolean filter(Circuit circuit);
    }

    private final TorRandom random = new TorRandom();

    private final Set<Circuit> activeCircuits;

    public ExitActiveCircuits() {
        this.activeCircuits = new HashSet<Circuit>();
    }

    public void addActiveCircuit(Circuit circuit) {
        synchronized (activeCircuits) {
            activeCircuits.add(circuit);
            activeCircuits.notifyAll();
        }
    }

    public void removeActiveCircuit(Circuit circuit) {
        synchronized (activeCircuits) {
            activeCircuits.remove(circuit);
        }
    }

    public int getActiveCircuitCount() {
        synchronized (activeCircuits) {
            return activeCircuits.size();
        }
    }

    public Set<Circuit> getCircuitsByFilter(CircuitFilter filter) {
        final Set<Circuit> result = new HashSet<Circuit>();
        final Set<Circuit> circuits = new HashSet<Circuit>();

        synchronized (activeCircuits) {
            // the filter might lock additional objects, causing a deadlock, so don't
            // call it inside the monitor
            circuits.addAll(activeCircuits);
        }

        for (Circuit c : circuits) {
            if (filter == null || filter.filter(c)) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Circuit> getRandomlyOrderedListOfExitCircuits() {
        final Set<Circuit> notDirectory = getCircuitsByFilter(new CircuitFilter() {

            public boolean filter(Circuit circuit) {
                return !circuit.isMarkedForClose() && circuit.isConnected();
            }
        });
        final ArrayList<Circuit> ac = new ArrayList<Circuit>();
        for (Circuit c : notDirectory) {
            ac.add(c);
        }
        final int sz = ac.size();
        for (int i = 0; i < sz; i++) {
            final Circuit tmp = ac.get(i);
            final int swapIdx = random.nextInt(sz);
            ac.set(i, ac.get(swapIdx));
            ac.set(swapIdx, tmp);
        }
        return ac;
    }

    public void close() {
        ArrayList<Circuit> circuits;
        synchronized (activeCircuits) {
            circuits = new ArrayList<Circuit>(activeCircuits);
        }
        for (Circuit c : circuits) {
            c.destroyCircuit();
        }
    }
}
