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
import im.actor.torlib.circuits.actors.path.DirectoryCircuitFactory;
import im.actor.torlib.circuits.streams.TorStream;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class DirectoryCircuitsActor extends TypedActor<DirectoryCircuitsInt> implements DirectoryCircuitsInt {

    public static DirectoryCircuitsInt get(final CircuitManager circuitManager) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(DirectoryCircuitsActor.class,
                new ActorCreator<DirectoryCircuitsActor>() {
                    @Override
                    public DirectoryCircuitsActor create() {
                        return new DirectoryCircuitsActor(circuitManager);
                    }
                }), "/tor/circuit/dir"), DirectoryCircuitsInt.class);
    }

    private final static int OPEN_DIRECTORY_STREAM_TIMEOUT = 10 * 1000;

    private CircuitManager manager;

    public DirectoryCircuitsActor(CircuitManager manager) {
        super(DirectoryCircuitsInt.class);
        this.manager = manager;
    }

    @Override
    public Future<TorStream> openDirectoryStream() {
        final TypedFuture<TorStream> res = future();
        ask(CircuitBuildActor.build(new DirectoryCircuitFactory(manager),
                manager.getConnectionCache()), new AskCallback<DirectoryCircuit>() {

            @Override
            public void onResult(DirectoryCircuit result) {
                try {
                    TorStream torStream = CircuitStreamFactory.
                            openDirectoryStream(result, OPEN_DIRECTORY_STREAM_TIMEOUT, true);
                    res.doComplete(torStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    res.doError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                res.doError(throwable);
            }
        });
        return res;
    }
}
