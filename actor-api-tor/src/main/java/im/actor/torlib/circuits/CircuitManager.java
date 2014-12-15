package im.actor.torlib.circuits;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.circuits.build.CircuitCreationActor;
import im.actor.torlib.circuits.build.CircuitCreationInt;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.circuits.hs.HiddenServiceManager;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.dashboard.DashboardRenderable;
import im.actor.torlib.dashboard.DashboardRenderer;
import im.actor.utils.IPv4Address;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.Threading;


public class CircuitManager implements DashboardRenderable {

    private final static int OPEN_DIRECTORY_STREAM_RETRY_COUNT = 5;

    public interface CircuitFilter {
        boolean filter(Circuit circuit);
    }

    private final ConnectionCache connectionCache;
    private final Set<CircuitImpl> activeCircuits;
    private final Queue<InternalCircuit> cleanInternalCircuits;
    private int requestedInternalCircuitCount = 0;
    private int pendingInternalCircuitCount = 0;
    private final TorRandom random;
    // private final PendingExitStreams pendingExitStreams;
    private final CircuitCreationInt circuitCreationActor;
    private final CircuitPathChooser pathChooser;
    private final HiddenServiceManager hiddenServiceManager;
    private final ReentrantLock lock = Threading.lock("circuitManager");

    private boolean isBuilding = false;

    public CircuitManager(TorConfig config, NewDirectory directory, ConnectionCache connectionCache) {

        this.connectionCache = connectionCache;
        this.pathChooser = CircuitPathChooser.create(config, directory);

        this.hiddenServiceManager = new HiddenServiceManager(config, directory, this);
        this.circuitCreationActor = CircuitCreationActor.get(config, directory, connectionCache, pathChooser, this,
                hiddenServiceManager);
        this.activeCircuits = new HashSet<CircuitImpl>();
        this.cleanInternalCircuits = new LinkedList<InternalCircuit>();
        this.random = new TorRandom();

    }

    public void startBuildingCircuits() {
        lock.lock();
        try {
            isBuilding = true;
            circuitCreationActor.start();
        } finally {
            lock.unlock();
        }
    }

    public void stopBuildingCircuits(boolean killCircuits) {
        lock.lock();
        try {
            isBuilding = false;
            circuitCreationActor.stop();
        } finally {
            lock.unlock();
        }

        if (killCircuits) {
            ArrayList<CircuitImpl> circuits;
            synchronized (activeCircuits) {
                circuits = new ArrayList<CircuitImpl>(activeCircuits);
            }
            for (CircuitImpl c : circuits) {
                c.destroyCircuit();
            }
        }
    }


    public void addActiveCircuit(CircuitImpl circuit) {
        synchronized (activeCircuits) {
            activeCircuits.add(circuit);
            activeCircuits.notifyAll();
        }

        boolean doDestroy;
        lock.lock();
        try {
            doDestroy = !isBuilding;
        } finally {
            lock.unlock();
        }

        if (doDestroy) {
            // we were asked to stop since this circuit was started
            circuit.destroyCircuit();
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
        lock.lock();
        try {
            return getPendingCircuits().size();
        } finally {
            lock.unlock();
        }
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

    public Future<TorStream> openExitStreamTo(String hostname, int port) {
        return circuitCreationActor.openExitStream(hostname, port, 15000);
    }

    public Future<TorStream> openExitStreamTo(IPv4Address address, int port) {
        return circuitCreationActor.openExitStream(address, port, 15000);
    }

    public InternalCircuit getCleanInternalCircuit() throws InterruptedException {
        synchronized (cleanInternalCircuits) {
            try {
                requestedInternalCircuitCount += 1;
                while (cleanInternalCircuits.isEmpty()) {
                    cleanInternalCircuits.wait();
                }
                return cleanInternalCircuits.remove();
            } finally {
                requestedInternalCircuitCount -= 1;
            }
        }
    }

    public int getNeededCleanCircuitCount(boolean isPredicted) {
        synchronized (cleanInternalCircuits) {
            final int predictedCount = (isPredicted) ? 2 : 0;
            final int needed = Math.max(requestedInternalCircuitCount, predictedCount) - (pendingInternalCircuitCount + cleanInternalCircuits.size());
            if (needed < 0) {
                return 0;
            } else {
                return needed;
            }
        }
    }

    public void incrementPendingInternalCircuitCount() {
        synchronized (cleanInternalCircuits) {
            pendingInternalCircuitCount += 1;
        }
    }

    public void decrementPendingInternalCircuitCount() {
        synchronized (cleanInternalCircuits) {
            pendingInternalCircuitCount -= 1;
        }
    }

    public void addCleanInternalCircuit(InternalCircuit circuit) {
        synchronized (cleanInternalCircuits) {
            pendingInternalCircuitCount -= 1;
            cleanInternalCircuits.add(circuit);
            cleanInternalCircuits.notifyAll();
        }
    }


    public void dashboardRender(DashboardRenderer renderer, PrintWriter writer, int flags) throws IOException {
        if ((flags & DASHBOARD_CIRCUITS) == 0) {
            return;
        }
        renderer.renderComponent(writer, flags, connectionCache);
        // renderer.renderComponent(writer, flags, circuitCreationTask.getCircuitPredictor());
        writer.println("[Circuit Manager]");
        writer.println();
        for (Circuit c : getCircuitsByFilter(null)) {
            renderer.renderComponent(writer, flags, c);
        }
    }

    // Obsolete directory circuits

    @Deprecated
    public DirectoryCircuit openDirectoryCircuit() throws OpenFailedException {
        int failCount = 0;
        while (failCount < OPEN_DIRECTORY_STREAM_RETRY_COUNT) {
            final DirectoryCircuit circuit = new DirectoryCircuitImpl(this, null);
            if (tryOpenDirectoryCircuit(circuit)) {
                return circuit;
            }
            failCount += 1;
        }
        throw new OpenFailedException("Could not create circuit for directory stream");
    }

    @Deprecated
    public DirectoryCircuit openDirectoryCircuitTo(Router destRouter) throws OpenFailedException {
        final DirectoryCircuit circuit = new DirectoryCircuitImpl(this, Arrays.asList(destRouter));
        if (!tryOpenDirectoryCircuit(circuit)) {
            throw new OpenFailedException("Could not create directory circuit for path");
        }
        return circuit;
    }

    @Deprecated
    private boolean tryOpenDirectoryCircuit(Circuit circuit) {
        final DirectoryCircuitResult result = new DirectoryCircuitResult();
        final CircuitCreationRequest req = new CircuitCreationRequest(pathChooser, circuit, result, true);
        final CircuitBuildTask task = new CircuitBuildTask(req, connectionCache);
        task.run();
        return result.isSuccessful();
    }

    private static class DirectoryCircuitResult implements CircuitBuildHandler {

        private boolean isFailed;

        public void connectionCompleted(Connection connection) {
        }

        public void nodeAdded(CircuitNode node) {
        }

        public void circuitBuildCompleted(Circuit circuit) {
        }

        public void connectionFailed(String reason) {
            isFailed = true;
        }

        public void circuitBuildFailed(String reason) {
            isFailed = true;
        }

        boolean isSuccessful() {
            return !isFailed;
        }
    }
}
