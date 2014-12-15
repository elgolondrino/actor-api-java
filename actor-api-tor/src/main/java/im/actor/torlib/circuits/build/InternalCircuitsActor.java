package im.actor.torlib.circuits.build;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.build.path.InternalCircuitFactory;
import im.actor.torlib.connections.Connection;
import im.actor.utils.Threading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class InternalCircuitsActor extends TypedActor<InternalCircuitsInt> implements InternalCircuitsInt {

    public static InternalCircuitsInt get(final CircuitManager circuitManager) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(InternalCircuitsActor.class,
                new ActorCreator<InternalCircuitsActor>() {
                    @Override
                    public InternalCircuitsActor create() {
                        return new InternalCircuitsActor(circuitManager);
                    }
                }), "/tor/circuit/internal"), InternalCircuitsInt.class);
    }

    private final Executor executor;

    private final Queue<InternalCircuit> cleanInternalCircuits;
    private Queue<TypedFuture<InternalCircuit>> pending;
    private int pendingInternalCircuitCount = 0;

    private final CircuitBuildHandler internalBuildHandler;

    private CircuitManager manager;

    public InternalCircuitsActor(CircuitManager manager) {
        super(InternalCircuitsInt.class);
        this.manager = manager;
        this.cleanInternalCircuits = new LinkedList<InternalCircuit>();
        this.pending = new LinkedList<TypedFuture<InternalCircuit>>();
        this.executor = Threading.newPool("InternalCircuitsActor worker");
        this.internalBuildHandler = createInternalCircuitBuildHandler();
    }

    @Override
    public void start() {
        self().send(new Iterate());
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof Iterate) {
            maybeBuildInternalCircuit();
            self().sendOnce(new Iterate(), 5000);
        } else if (message instanceof ConnectionFailed) {
            decrementPendingInternalCircuitCount();
        } else if (message instanceof ConnectionCompleted) {
            addCleanInternalCircuit(((ConnectionCompleted) message).circuit);
        }
    }

    @Override
    public Future<InternalCircuit> pickInternalCircuit() {
        if (cleanInternalCircuits.isEmpty()) {
            TypedFuture<InternalCircuit> res = future();
            pending.add(res);
            return res;
        } else {
            return result(cleanInternalCircuits.remove());
        }
    }

    private void maybeBuildInternalCircuit() {
        final int needed = getNeededCleanCircuitCount();

        if (needed > 0) {
            launchBuildTaskForInternalCircuit();
        }
    }

    private void launchBuildTaskForInternalCircuit() {
        final CircuitCreationRequest request = new CircuitCreationRequest(
                new InternalCircuitFactory(manager), internalBuildHandler);
        final CircuitBuildTask task = new CircuitBuildTask(request, manager.getConnectionCache());
        executor.execute(task);
        incrementPendingInternalCircuitCount();
    }

    public int getNeededCleanCircuitCount() {
        synchronized (cleanInternalCircuits) {
            final int needed = Math.max(pending.size(), 2) - (pendingInternalCircuitCount + cleanInternalCircuits.size());
            if (needed < 0) {
                return 0;
            } else {
                return needed;
            }
        }
    }

    public void incrementPendingInternalCircuitCount() {
        pendingInternalCircuitCount += 1;
    }

    public void decrementPendingInternalCircuitCount() {
        pendingInternalCircuitCount -= 1;
    }

    public void addCleanInternalCircuit(InternalCircuit circuit) {
        pendingInternalCircuitCount -= 1;

        if (pending.isEmpty()) {
            cleanInternalCircuits.add(circuit);
        } else {
            pending.remove().doComplete(circuit);
        }
    }

    @Override
    public void stop() {
        context().stopSelf();
    }

    private CircuitBuildHandler createInternalCircuitBuildHandler() {
        return new CircuitBuildHandler() {

            public void nodeAdded(CircuitNodeImpl node) {

            }

            public void connectionFailed(String reason) {
                self().send(new ConnectionFailed());
            }

            public void connectionCompleted(Connection connection) {

            }

            public void circuitBuildFailed(String reason) {
                self().send(new ConnectionFailed());
            }

            public void circuitBuildCompleted(Circuit circuit) {
                self().send(new ConnectionCompleted((InternalCircuit) circuit));
            }
        };
    }

    private static class Iterate {

    }

    private static class ConnectionFailed {

    }

    private static class ConnectionCompleted {
        InternalCircuit circuit;

        public ConnectionCompleted(InternalCircuit circuit) {
            this.circuit = circuit;
        }
    }
}
