package im.actor.torlib.circuits.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.build.path.ExitCircuitFactory;
import im.actor.torlib.circuits.hs.HiddenServiceManager;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.directory.routers.exitpolicy.ExitTarget;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.directory.routers.exitpolicy.PredictedPortTarget;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.IPv4Address;
import im.actor.utils.Threading;

public class ExitCircuitsActor extends TypedActor<ExitCircuitsInt> implements ExitCircuitsInt {


    public static ExitCircuitsInt get(final CircuitManager circuitManager) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(ExitCircuitsActor.class,
                new ActorCreator<ExitCircuitsActor>() {
                    @Override
                    public ExitCircuitsActor create() {
                        return new ExitCircuitsActor(circuitManager);
                    }
                }), "/tor/circuit/build"), ExitCircuitsInt.class);
    }

    private final static Logger logger = Logger.getLogger(ExitCircuitsActor.class.getName());
    private final static int MAX_CIRCUIT_DIRTINESS = 300; // seconds
    private final static int MAX_PENDING_CIRCUITS = 4;

    private final NewDirectory directory;
    private final HiddenServiceManager hiddenServiceManager;
    private final ConnectionCache connectionCache;
    private final CircuitManager circuitManager;
    private final Executor executor;
    private final CircuitBuildHandler buildHandler;
    // To avoid obnoxiously printing a warning every second
    private int notEnoughDirectoryInformationWarningCounter = 0;

    private final ExitCircuitsPredictor predictor;

    private final Set<StreamExitRequest> pendingRequests;

    public ExitCircuitsActor(CircuitManager circuitManager) {
        super(ExitCircuitsInt.class);

        this.circuitManager = circuitManager;
        this.directory = circuitManager.getDirectory();
        this.connectionCache = circuitManager.getConnectionCache();

        this.hiddenServiceManager = new HiddenServiceManager(circuitManager.getConfig(), directory, circuitManager);
        this.pendingRequests = new CopyOnWriteArraySet<StreamExitRequest>();
        this.executor = Threading.newPool("ExitCircuitsActor worker");
        this.buildHandler = createCircuitBuildHandler();
        this.predictor = new ExitCircuitsPredictor();

    }

    @Override
    public void start() {
        self().sendOnce(new Iterate());
    }

    @Override
    public Future<TorStream> openExitStream(final String hostname, final int port, long timeout) {
        if (hostname.endsWith(".onion")) {
            // TODO: Better implementation
            final TypedFuture<TorStream> res = future();
            new Thread() {
                @Override
                public void run() {
                    try {
                        res.doComplete(hiddenServiceManager.getStreamTo(hostname, port));
                    } catch (OpenFailedException e) {
                        e.printStackTrace();
                        res.doError(e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        res.doError(e);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        res.doError(e);
                    }
                }
            }.start();
            return res;
        }
        TypedFuture<TorStream> res = future();
        predictor.addExitPortRequest(port);
        pendingRequests.add(new StreamExitRequest(hostname, port, res));
        return res;
    }

    @Override
    public Future<TorStream> openExitStream(IPv4Address address, int port, long timeout) {
        TypedFuture<TorStream> res = future();
        predictor.addExitPortRequest(port);
        pendingRequests.add(new StreamExitRequest(address, port, res));
        return res;
    }

    @Override
    public void stop() {
        context().stopSelf();
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof Iterate) {
            expireOldCircuits();
            assignPendingStreamsToActiveCircuits();
            checkCircuitsForCreation();
            self().sendOnce(new Iterate(), 5000);
        }
    }

    private void assignPendingStreamsToActiveCircuits() {
        if (pendingRequests.isEmpty())
            return;

        for (ExitCircuit c : circuitManager.getExitActiveCircuits().getRandomlyOrderedListOfExitCircuits()) {
            final Iterator<StreamExitRequest> it = pendingRequests.iterator();
            while (it.hasNext()) {
                StreamExitRequest req = it.next();
                if (attemptHandleStreamRequest(c, req)) {
                    pendingRequests.remove(req);
                }
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
        final ExitCircuitStreamTask task = new ExitCircuitStreamTask(circuit, exitRequest);
        executor.execute(task);
    }

    private void expireOldCircuits() {
        final Set<Circuit> circuits = circuitManager.getExitActiveCircuits().getCircuitsByFilter(new ExitActiveCircuits.CircuitFilter() {

            public boolean filter(Circuit circuit) {
                return !circuit.isMarkedForClose() && circuit.getSecondsDirty() > MAX_CIRCUIT_DIRTINESS;
            }
        });
        for (Circuit c : circuits) {
            logger.fine("Closing idle dirty circuit: " + c);
            c.markForClose();
        }
    }


    private void checkCircuitsForCreation() {

        if (!directory.haveMinimumRouterInfo()) {
            if (notEnoughDirectoryInformationWarningCounter % 20 == 0)
                logger.info("Cannot build circuits because we don't have enough directory information");
            notEnoughDirectoryInformationWarningCounter++;
            return;
        }

        buildCircuitIfNeeded();
    }

    private void buildCircuitIfNeeded() {
        if (connectionCache.isClosed()) {
            logger.warning("Not building circuits, because connection cache is closed");
            return;
        }

        final List<PredictedPortTarget> predictedPorts = predictor.getPredictedPortTargets();
        final List<ExitTarget> exitTargets = new ArrayList<ExitTarget>();
        for (StreamExitRequest streamRequest : pendingRequests) {
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


    private int countCircuitsSupportingTarget(final ExitTarget target, final boolean needClean) {
        final ExitActiveCircuits.CircuitFilter filter = new ExitActiveCircuits.CircuitFilter() {
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
        return circuitManager.getExitActiveCircuits().getCircuitsByFilter(filter).size();
    }

    private void buildCircuitToHandleExitTargets(List<ExitTarget> exitTargets) {
        if (exitTargets.isEmpty()) {
            return;
        }
        if (!directory.haveMinimumRouterInfo())
            return;
        if (circuitManager.getExitActiveCircuits().getPendingCircuitCount() >= MAX_PENDING_CIRCUITS)
            return;

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Building new circuit to handle " + exitTargets.size() + " pending streams and predicted ports");
        }

        launchBuildTaskForTargets(exitTargets);
    }

    private void launchBuildTaskForTargets(List<ExitTarget> exitTargets) {
        final CircuitCreationRequest request = new CircuitCreationRequest(
                new ExitCircuitFactory(exitTargets, circuitManager), buildHandler);
        final CircuitBuildTask task = new CircuitBuildTask(request, connectionCache);
        executor.execute(task);
    }

    private CircuitBuildHandler createCircuitBuildHandler() {
        return new CircuitBuildHandler() {

            public void circuitBuildCompleted(Circuit circuit) {
                logger.fine("Circuit completed to: " + circuit);
                circuitOpenedHandler(circuit);
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
        for (StreamExitRequest req : pendingRequests) {
            if (ec.canHandleExitTo(req) && req.reserveRequest()) {
                launchExitStreamTask(ec, req);
            }
        }
    }


    private static class Iterate {

    }
}
