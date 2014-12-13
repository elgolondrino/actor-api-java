package im.actor.torlib;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.guards.EntryGuards;
import im.actor.torlib.circuits.hs.HiddenServiceManager;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.dashboard.DashboardRenderable;
import im.actor.torlib.dashboard.DashboardRenderer;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.directory.Directory;
import im.actor.torlib.directory.DirectoryDownloader;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.state.TorInitializationTracker;
import im.actor.utils.Threading;


public class CircuitManager implements DashboardRenderable {

    public static final int DIRECTORY_PURPOSE_CONSENSUS = 1;
    public static final int DIRECTORY_PURPOSE_CERTIFICATES = 2;
    public static final int DIRECTORY_PURPOSE_DESCRIPTORS = 3;

    private final static int OPEN_DIRECTORY_STREAM_RETRY_COUNT = 5;
    private final static int OPEN_DIRECTORY_STREAM_TIMEOUT = 10 * 1000;

    public interface CircuitFilter {
        boolean filter(Circuit circuit);
    }

    private final TorConfig config;
    private final ConnectionCache connectionCache;
    private final Set<CircuitImpl> activeCircuits;
    private final Queue<InternalCircuit> cleanInternalCircuits;
    private int requestedInternalCircuitCount = 0;
    private int pendingInternalCircuitCount = 0;
    private final TorRandom random;
    private final PendingExitStreams pendingExitStreams;
    private final ScheduledExecutorService scheduledExecutor = Threading.newSingleThreadScheduledPool("CircuitManager worker");
    private final CircuitCreationTask circuitCreationTask;
    private final TorInitializationTracker initializationTracker;
    private final CircuitPathChooser pathChooser;
    private final HiddenServiceManager hiddenServiceManager;
    private final ReentrantLock lock = Threading.lock("circuitManager");

    private boolean isBuilding = false;

    public CircuitManager(TorConfig config, NewDirectory directory, ConnectionCache connectionCache, TorInitializationTracker initializationTracker) {
        this.config = config;
        this.connectionCache = connectionCache;
        this.pathChooser = CircuitPathChooser.create(config, directory);
//        if (config.getUseEntryGuards() || config.getUseBridges()) {
//            this.pathChooser.enableEntryGuards(new EntryGuards(config, connectionCache, directoryDownloader, directory));
//        }
        this.pendingExitStreams = new PendingExitStreams(config);
        this.circuitCreationTask = new CircuitCreationTask(config, directory, connectionCache, pathChooser, this, initializationTracker);
        this.activeCircuits = new HashSet<CircuitImpl>();
        this.cleanInternalCircuits = new LinkedList<InternalCircuit>();
        this.random = new TorRandom();

        this.initializationTracker = initializationTracker;
        this.hiddenServiceManager = new HiddenServiceManager(config, directory, this);

        // directoryDownloader.setCircuitManager(this);
    }

    public void startBuildingCircuits() {
        lock.lock();
        try {
            isBuilding = true;
            scheduledExecutor.scheduleAtFixedRate(circuitCreationTask, 0, 1000, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public void stopBuildingCircuits(boolean killCircuits) {
        lock.lock();
        try {
            isBuilding = false;
            scheduledExecutor.shutdownNow();
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

    public ExitCircuit createNewExitCircuit(Router exitRouter) {
        return CircuitImpl.createExitCircuit(this, exitRouter);
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

    public TorStream openExitStreamTo(String hostname, int port)
            throws InterruptedException, TimeoutException, OpenFailedException {
        if (hostname.endsWith(".onion")) {
            return hiddenServiceManager.getStreamTo(hostname, port);
        }
        validateHostname(hostname);
        circuitCreationTask.predictPort(port);
        return pendingExitStreams.openExitStream(hostname, port);
    }

    private void validateHostname(String hostname) throws OpenFailedException {
        maybeRejectInternalAddress(hostname);
        if (hostname.toLowerCase().endsWith(".onion")) {
            throw new OpenFailedException("Hidden services not supported");
        } else if (hostname.toLowerCase().endsWith(".exit")) {
            throw new OpenFailedException(".exit addresses are not supported");
        }
    }

    private void maybeRejectInternalAddress(String hostname) throws OpenFailedException {
        if (IPv4Address.isValidIPv4AddressString(hostname)) {
            maybeRejectInternalAddress(IPv4Address.createFromString(hostname));
        }
    }

    private void maybeRejectInternalAddress(IPv4Address address) throws OpenFailedException {
        final InetAddress inetAddress = address.toInetAddress();
        if (inetAddress.isSiteLocalAddress() && config.getClientRejectInternalAddress()) {
            throw new OpenFailedException("Rejecting stream target with internal address: " + address);
        }
    }

    public TorStream openExitStreamTo(IPv4Address address, int port)
            throws InterruptedException, TimeoutException, OpenFailedException {
        maybeRejectInternalAddress(address);
        circuitCreationTask.predictPort(port);
        return pendingExitStreams.openExitStream(address, port);
    }

    public List<StreamExitRequest> getPendingExitStreams() {
        return pendingExitStreams.getUnreservedPendingRequests();
    }

    public DirectoryCircuit openDirectoryCircuit() throws OpenFailedException {
        int failCount = 0;
        while (failCount < OPEN_DIRECTORY_STREAM_RETRY_COUNT) {
            final DirectoryCircuit circuit = CircuitImpl.createDirectoryCircuit(this);
            if (tryOpenCircuit(circuit, true, true)) {
                return circuit;
            }
            failCount += 1;
        }
        throw new OpenFailedException("Could not create circuit for directory stream");
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

    public void dashboardRender(DashboardRenderer renderer, PrintWriter writer, int flags) throws IOException {
        if ((flags & DASHBOARD_CIRCUITS) == 0) {
            return;
        }
        renderer.renderComponent(writer, flags, connectionCache);
        renderer.renderComponent(writer, flags, circuitCreationTask.getCircuitPredictor());
        writer.println("[Circuit Manager]");
        writer.println();
        for (Circuit c : getCircuitsByFilter(null)) {
            renderer.renderComponent(writer, flags, c);
        }
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

    public DirectoryCircuit openDirectoryCircuitTo(List<Router> path) throws OpenFailedException {
        final DirectoryCircuit circuit = CircuitImpl.createDirectoryCircuitTo(this, path);
        if (!tryOpenCircuit(circuit, true, false)) {
            throw new OpenFailedException("Could not create directory circuit for path");
        }
        return circuit;
    }

    public ExitCircuit openExitCircuitTo(List<Router> path) throws OpenFailedException {
        final ExitCircuit circuit = CircuitImpl.createExitCircuitTo(this, path);
        if (!tryOpenCircuit(circuit, false, false)) {
            throw new OpenFailedException("Could not create exit circuit for path");
        }
        return circuit;
    }

    public InternalCircuit openInternalCircuitTo(List<Router> path) throws OpenFailedException {
        final InternalCircuit circuit = CircuitImpl.createInternalCircuitTo(this, path);
        if (!tryOpenCircuit(circuit, false, false)) {
            throw new OpenFailedException("Could not create internal circuit for path");
        }
        return circuit;
    }

    private boolean tryOpenCircuit(Circuit circuit, boolean isDirectory, boolean trackInitialization) {
        final DirectoryCircuitResult result = new DirectoryCircuitResult();
        final CircuitCreationRequest req = new CircuitCreationRequest(pathChooser, circuit, result, isDirectory);
        final CircuitBuildTask task = new CircuitBuildTask(req, connectionCache, (trackInitialization) ? (initializationTracker) : (null));
        task.run();
        return result.isSuccessful();
    }
}
