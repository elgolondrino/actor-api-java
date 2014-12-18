package im.actor.torlib.circuits.actors;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.tasks.AskCallback;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.actors.path.InternalCircuitFactory;

import java.util.LinkedList;
import java.util.Queue;

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

    private static final int CACHED_CIRCUITS = 2;

    private final Queue<Circuit> cleanInternalCircuits;
    private Queue<TypedFuture<Circuit>> pending;
    private int pendingInternalCircuitCount = 0;


    private CircuitManager manager;

    public InternalCircuitsActor(CircuitManager manager) {
        super(InternalCircuitsInt.class);
        this.manager = manager;
        this.cleanInternalCircuits = new LinkedList<Circuit>();
        this.pending = new LinkedList<TypedFuture<Circuit>>();
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
        }
    }

    @Override
    public Future<Circuit> pickInternalCircuit() {
        if (cleanInternalCircuits.isEmpty()) {
            TypedFuture<Circuit> res = future();
            pending.add(res);
            self().sendOnce(new Iterate());
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
        ask(CircuitBuildActor.build(new InternalCircuitFactory(manager), manager.getConnectionCache()),
                new AskCallback<Circuit>() {
                    @Override
                    public void onResult(Circuit result) {
                        pendingInternalCircuitCount--;

                        if (pending.isEmpty()) {
                            cleanInternalCircuits.add(result);
                        } else {
                            pending.remove().doComplete(result);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        pendingInternalCircuitCount--;
                    }
                });
        pendingInternalCircuitCount++;
    }

    public int getNeededCleanCircuitCount() {
        synchronized (cleanInternalCircuits) {
            final int needed = Math.max(pending.size(), CACHED_CIRCUITS) - (pendingInternalCircuitCount + cleanInternalCircuits.size());
            if (needed < 0) {
                return 0;
            } else {
                return needed;
            }
        }
    }

    @Override
    public void stop() {
        context().stopSelf();
    }

    private static class Iterate {

    }
}
