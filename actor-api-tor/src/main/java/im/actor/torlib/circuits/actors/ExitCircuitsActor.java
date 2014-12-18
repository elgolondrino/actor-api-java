package im.actor.torlib.circuits.actors;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.tasks.AskCallback;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.actors.path.ExitCircuitFactory;
import im.actor.torlib.circuits.actors.target.ExitCircuitStreamRequest;
import im.actor.torlib.circuits.actors.target.ExitTarget;
import im.actor.torlib.circuits.actors.target.PredictedPortTarget;
import im.actor.torlib.circuits.hs.HiddenServiceManager;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.connections.ConnectionCache;
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

    private final HiddenServiceManager hiddenServiceManager;
    private final ConnectionCache connectionCache;
    private final CircuitManager circuitManager;
    private final Executor executor;

    private final ExitCircuitsPredictor predictor;

    private final Set<ExitCircuitStreamRequest> pendingRequests;

    public ExitCircuitsActor(CircuitManager circuitManager) {
        super(ExitCircuitsInt.class);

        this.circuitManager = circuitManager;
        this.connectionCache = circuitManager.getConnectionCache();

        this.hiddenServiceManager = new HiddenServiceManager(circuitManager.getDirectory(), circuitManager);
        this.pendingRequests = new CopyOnWriteArraySet<ExitCircuitStreamRequest>();
        this.executor = Threading.newPool("ExitCircuitsActor worker");

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
        pendingRequests.add(new ExitCircuitStreamRequest(hostname, port, res));
        self().sendOnce(new Iterate());
        return res;
    }

    @Override
    public Future<TorStream> openExitStream(IPv4Address address, int port, long timeout) {
        TypedFuture<TorStream> res = future();
        predictor.addExitPortRequest(port);
        pendingRequests.add(new ExitCircuitStreamRequest(address, port, res));
        self().sendOnce(new Iterate());
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
            buildCircuitIfNeeded();
            self().sendOnce(new Iterate(), 5000);
        }
    }

    private void assignPendingStreamsToActiveCircuits() {
        if (pendingRequests.isEmpty())
            return;

        for (Circuit c : circuitManager.getExitActiveCircuits().getRandomlyOrderedListOfExitCircuits()) {
            final Iterator<ExitCircuitStreamRequest> it = pendingRequests.iterator();
            while (it.hasNext()) {
                ExitCircuitStreamRequest req = it.next();
                if (attemptHandleStreamRequest(c, req)) {
                    pendingRequests.remove(req);
                }
            }
        }
    }

    private boolean attemptHandleStreamRequest(Circuit c, ExitCircuitStreamRequest request) {
        if (canHandleExitTo(c, request)) {
            if (request.reserveRequest()) {
                launchExitStreamTask(c, request);
            }
            // else request is reserved meaning another circuit is already trying to handle it
            return true;
        }
        return false;
    }

    private void launchExitStreamTask(Circuit circuit, ExitCircuitStreamRequest exitRequest) {
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

    private void buildCircuitIfNeeded() {
        if (connectionCache.isClosed()) {
            logger.warning("Not building circuits, because connection cache is closed");
            return;
        }

        final List<PredictedPortTarget> predictedPorts = predictor.getPredictedPortTargets();
        final List<ExitTarget> exitTargets = new ArrayList<ExitTarget>();
        for (ExitCircuitStreamRequest streamRequest : pendingRequests) {
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
                final boolean pendingOrConnected = circuit.isConnected();
                final boolean isCleanIfNeeded = !(needClean && !circuit.isClean());
                return pendingOrConnected && isCleanIfNeeded && canHandleExitTo(circuit, target);
            }
        };
        return circuitManager.getExitActiveCircuits().getCircuitsByFilter(filter).size();
    }

    private void buildCircuitToHandleExitTargets(List<ExitTarget> exitTargets) {
        if (exitTargets.isEmpty()) {
            return;
        }
//        if (!directory.haveMinimumRouterInfo())
//            return;

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Building new circuit to handle " + exitTargets.size() + " pending streams and predicted ports");
        }

        ask(CircuitBuildActor.build(new ExitCircuitFactory(exitTargets, circuitManager), connectionCache),
                new AskCallback<Circuit>() {
                    @Override
                    public void onResult(Circuit circuit) {
                        circuitManager.getExitActiveCircuits().addActiveCircuit(circuit);
                        for (ExitCircuitStreamRequest req : pendingRequests) {
                            if (canHandleExitTo(circuit, req) && req.reserveRequest()) {
                                launchExitStreamTask(circuit, req);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // Try Again
                        self().sendOnce(new Iterate(), 1000);
                    }
                });
    }

    private boolean canHandleExitTo(Circuit circuit, ExitTarget target) {
        if (circuit.isMarkedForClose()) {
            return false;
        }
        if (target.isAddressTarget()) {
            return circuit.getLastRouter().exitPolicyAccepts(target.getAddress(), target.getPort());
        } else {
            return circuit.getLastRouter().exitPolicyAccepts(target.getPort());
        }
    }


    private static class Iterate {

    }

    public static class ExitCircuitsPredictor {

        private final static long TIMEOUT_MS = 60 * 60 * 1000; // One hour

        private final Map<Integer, Long> portsSeen;

        public ExitCircuitsPredictor() {
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
            final Iterator<Map.Entry<Integer, Long>> it = portsSeen.entrySet().iterator();
            while (it.hasNext()) {
                if ((now - it.next().getValue()) > TIMEOUT_MS) {
                    it.remove();
                }
            }
        }
    }
}
