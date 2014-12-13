package im.actor.torlib.circuits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import im.actor.torlib.*;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.Directory;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.data.exitpolicy.ExitTarget;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.state.TorInitializationTracker;
import im.actor.utils.Threading;

public class CircuitCreationTask implements Runnable {
    private final static Logger logger = Logger.getLogger(CircuitCreationTask.class.getName());
    private final static int MAX_CIRCUIT_DIRTINESS = 300; // seconds
    private final static int MAX_PENDING_CIRCUITS = 4;

    private final TorConfig config;
    private final NewDirectory directory;
    private final ConnectionCache connectionCache;
    private final CircuitManager circuitManager;
    private final TorInitializationTracker initializationTracker;
    private final CircuitPathChooser pathChooser;
    private final Executor executor;
    private final CircuitBuildHandler buildHandler;
    private final CircuitBuildHandler internalBuildHandler;
    // To avoid obnoxiously printing a warning every second
    private int notEnoughDirectoryInformationWarningCounter = 0;

    private final CircuitPredictor predictor;

    private final AtomicLong lastNewCircuit;

    public CircuitCreationTask(TorConfig config, NewDirectory directory, ConnectionCache connectionCache, CircuitPathChooser pathChooser, CircuitManager circuitManager, TorInitializationTracker initializationTracker) {
        this.config = config;
        this.directory = directory;
        this.connectionCache = connectionCache;
        this.circuitManager = circuitManager;
        this.initializationTracker = initializationTracker;
        this.pathChooser = pathChooser;
        this.executor = Threading.newPool("CircuitCreationTask worker");
        this.buildHandler = createCircuitBuildHandler();
        this.internalBuildHandler = createInternalCircuitBuildHandler();
        this.predictor = new CircuitPredictor();
        this.lastNewCircuit = new AtomicLong();
    }

    public CircuitPredictor getCircuitPredictor() {
        return predictor;
    }

    public void run() {
        expireOldCircuits();
        assignPendingStreamsToActiveCircuits();
        checkExpiredPendingCircuits();
        checkCircuitsForCreation();
    }

    public void predictPort(int port) {
        predictor.addExitPortRequest(port);
    }

    private void assignPendingStreamsToActiveCircuits() {
        final List<StreamExitRequest> pendingExitStreams = circuitManager.getPendingExitStreams();
        if (pendingExitStreams.isEmpty())
            return;

        for (ExitCircuit c : circuitManager.getRandomlyOrderedListOfExitCircuits()) {
            final Iterator<StreamExitRequest> it = pendingExitStreams.iterator();
            while (it.hasNext()) {
                if (attemptHandleStreamRequest(c, it.next()))
                    it.remove();
            }
        }
    }

    private boolean attemptHandleStreamRequest(ExitCircuit c, StreamExitRequest request) {
        if (c.canHandleExitTo(request)) {
            if (request.reserveRequest()) {
                launchExitStreamTask(c, request);
            }
            // else request is reserved meaning another circuit is already trying to handle it
            return true;
        }
        return false;
    }

    private void launchExitStreamTask(ExitCircuit circuit, StreamExitRequest exitRequest) {
        final OpenExitStreamTask task = new OpenExitStreamTask(circuit, exitRequest);
        executor.execute(task);
    }

    private void expireOldCircuits() {
        final Set<Circuit> circuits = circuitManager.getCircuitsByFilter(new CircuitManager.CircuitFilter() {

            public boolean filter(Circuit circuit) {
                return !circuit.isMarkedForClose() && circuit.getSecondsDirty() > MAX_CIRCUIT_DIRTINESS;
            }
        });
        for (Circuit c : circuits) {
            logger.fine("Closing idle dirty circuit: " + c);
            ((CircuitImpl) c).markForClose();
        }
    }

    private void checkExpiredPendingCircuits() {
        // TODO Auto-generated method stub
    }

    private void checkCircuitsForCreation() {

        if (!directory.getObsoleteDirectory().haveMinimumRouterInfo()) {
            if (notEnoughDirectoryInformationWarningCounter % 20 == 0)
                logger.info("Cannot build circuits because we don't have enough directory information");
            notEnoughDirectoryInformationWarningCounter++;
            return;
        }


        if (lastNewCircuit.get() != 0) {
            final long now = System.currentTimeMillis();
            if ((now - lastNewCircuit.get()) < config.getNewCircuitPeriod()) {
                // return;
            }
        }

        buildCircuitIfNeeded();
        maybeBuildInternalCircuit();
    }

    private void buildCircuitIfNeeded() {
        if (connectionCache.isClosed()) {
            logger.warning("Not building circuits, because connection cache is closed");
            return;
        }

        final List<StreamExitRequest> pendingExitStreams = circuitManager.getPendingExitStreams();
        final List<PredictedPortTarget> predictedPorts = predictor.getPredictedPortTargets();
        final List<ExitTarget> exitTargets = new ArrayList<ExitTarget>();
        for (StreamExitRequest streamRequest : pendingExitStreams) {
            if (!streamRequest.isReserved() && countCircuitsSupportingTarget(streamRequest, false) == 0) {
                exitTargets.add(streamRequest);
            }
        }
        for (PredictedPortTarget ppt : predictedPorts) {
            if (countCircuitsSupportingTarget(ppt, true) < 2) {
                exitTargets.add(ppt);
            }
        }
        buildCircuitToHandleExitTargets(exitTargets);
    }

    private void maybeBuildInternalCircuit() {
        final int needed = circuitManager.getNeededCleanCircuitCount(predictor.isInternalPredicted());

        if (needed > 0) {
            launchBuildTaskForInternalCircuit();
        }
    }

    private void launchBuildTaskForInternalCircuit() {
        logger.fine("Launching new internal circuit");
        final InternalCircuitImpl circuit = new InternalCircuitImpl(circuitManager);
        final CircuitCreationRequest request = new CircuitCreationRequest(pathChooser, circuit, internalBuildHandler, false);
        final CircuitBuildTask task = new CircuitBuildTask(request, connectionCache);
        executor.execute(task);
        circuitManager.incrementPendingInternalCircuitCount();
    }

    private int countCircuitsSupportingTarget(final ExitTarget target, final boolean needClean) {
        final CircuitManager.CircuitFilter filter = new CircuitManager.CircuitFilter() {
            public boolean filter(Circuit circuit) {
                if (!(circuit instanceof ExitCircuit)) {
                    return false;
                }
                final ExitCircuit ec = (ExitCircuit) circuit;
                final boolean pendingOrConnected = circuit.isPending() || circuit.isConnected();
                final boolean isCleanIfNeeded = !(needClean && !circuit.isClean());
                return pendingOrConnected && isCleanIfNeeded && ec.canHandleExitTo(target);
            }
        };
        return circuitManager.getCircuitsByFilter(filter).size();
    }

    private void buildCircuitToHandleExitTargets(List<ExitTarget> exitTargets) {
        if (exitTargets.isEmpty()) {
            return;
        }
        if (!directory.getObsoleteDirectory().haveMinimumRouterInfo())
            return;
        if (circuitManager.getPendingCircuitCount() >= MAX_PENDING_CIRCUITS)
            return;

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Building new circuit to handle " + exitTargets.size() + " pending streams and predicted ports");
        }

        launchBuildTaskForTargets(exitTargets);
    }

    private void launchBuildTaskForTargets(List<ExitTarget> exitTargets) {
        final Router exitRouter = pathChooser.chooseExitNodeForTargets(exitTargets);
        if (exitRouter == null) {
            logger.warning("Failed to select suitable exit node for targets");
            return;
        }

        final Circuit circuit = circuitManager.createNewExitCircuit(exitRouter);
        final CircuitCreationRequest request = new CircuitCreationRequest(pathChooser, circuit, buildHandler, false);
        final CircuitBuildTask task = new CircuitBuildTask(request, connectionCache, initializationTracker);
        executor.execute(task);
    }

    private CircuitBuildHandler createCircuitBuildHandler() {
        return new CircuitBuildHandler() {

            public void circuitBuildCompleted(Circuit circuit) {
                logger.fine("Circuit completed to: " + circuit);
                circuitOpenedHandler(circuit);
                lastNewCircuit.set(System.currentTimeMillis());
            }

            public void circuitBuildFailed(String reason) {
                logger.fine("Circuit build failed: " + reason);
                buildCircuitIfNeeded();
            }

            public void connectionCompleted(Connection connection) {
                logger.finer("Circuit connection completed to " + connection);
            }

            public void connectionFailed(String reason) {
                logger.fine("Circuit connection failed: " + reason);
                buildCircuitIfNeeded();
            }

            public void nodeAdded(CircuitNode node) {
                logger.finer("Node added to circuit: " + node);
            }
        };
    }

    private void circuitOpenedHandler(Circuit circuit) {
        if (!(circuit instanceof ExitCircuit)) {
            return;
        }
        final ExitCircuit ec = (ExitCircuit) circuit;
        final List<StreamExitRequest> pendingExitStreams = circuitManager.getPendingExitStreams();
        for (StreamExitRequest req : pendingExitStreams) {
            if (ec.canHandleExitTo(req) && req.reserveRequest()) {
                launchExitStreamTask(ec, req);
            }
        }
    }

    private CircuitBuildHandler createInternalCircuitBuildHandler() {
        return new CircuitBuildHandler() {

            public void nodeAdded(CircuitNode node) {
                logger.finer("Node added to internal circuit: " + node);
            }

            public void connectionFailed(String reason) {
                logger.fine("Circuit connection failed: " + reason);
                circuitManager.decrementPendingInternalCircuitCount();
            }

            public void connectionCompleted(Connection connection) {
                logger.finer("Circuit connection completed to " + connection);
            }

            public void circuitBuildFailed(String reason) {
                logger.fine("Circuit build failed: " + reason);
                circuitManager.decrementPendingInternalCircuitCount();
            }

            public void circuitBuildCompleted(Circuit circuit) {
                logger.fine("Internal circuit build completed: " + circuit);
                lastNewCircuit.set(System.currentTimeMillis());
                circuitManager.addCleanInternalCircuit((InternalCircuit) circuit);
            }
        };
    }
}
