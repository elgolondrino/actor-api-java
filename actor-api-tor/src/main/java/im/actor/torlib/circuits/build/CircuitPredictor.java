package im.actor.torlib.circuits.build;

import im.actor.torlib.directory.routers.exitpolicy.PredictedPortTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CircuitPredictor {

    private final static long TIMEOUT_MS = 60 * 60 * 1000; // One hour

    private final Map<Integer, Long> portsSeen;

    public CircuitPredictor() {
        portsSeen = new HashMap<Integer, Long>();
        addExitPortRequest(80);
    }

    public void addExitPortRequest(int port) {
        synchronized (portsSeen) {
            portsSeen.put(port, System.currentTimeMillis());
        }
    }

    public List<PredictedPortTarget> getPredictedPortTargets() {

        synchronized (portsSeen) {
            removeExpiredPorts();

            final List<PredictedPortTarget> targets = new ArrayList<PredictedPortTarget>();
            for (int p : portsSeen.keySet()) {
                targets.add(new PredictedPortTarget(p));
            }
            return targets;
        }
    }

    private void removeExpiredPorts() {
        final long now = System.currentTimeMillis();
        final Iterator<Entry<Integer, Long>> it = portsSeen.entrySet().iterator();
        while (it.hasNext()) {
            if ((now - it.next().getValue()) > TIMEOUT_MS) {
                it.remove();
            }
        }
    }
}
