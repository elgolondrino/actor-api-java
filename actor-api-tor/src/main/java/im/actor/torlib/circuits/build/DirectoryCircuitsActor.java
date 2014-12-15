package im.actor.torlib.circuits.build;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.build.path.DirectoryCircuitFactory;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.Threading;

import java.util.concurrent.Executor;

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

    private final static int OPEN_DIRECTORY_STREAM_RETRY_COUNT = 5;

    private final static int OPEN_DIRECTORY_STREAM_TIMEOUT = 10 * 1000;

    private final Executor executor;
    private CircuitManager manager;

    public DirectoryCircuitsActor(CircuitManager manager) {
        super(DirectoryCircuitsInt.class);
        this.manager = manager;
        this.executor = Threading.newPool("DirectoryCircuitsActor worker");
    }

    @Override
    public Future<TorStream> openDirectoryStream() {
        final TypedFuture<TorStream> res = future();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int failCount = 0;
                while (failCount < OPEN_DIRECTORY_STREAM_RETRY_COUNT) {
                    DirectoryCircuit circuit = tryOpenDirectoryCircuit(new DirectoryCircuitFactory(manager));
                    if (circuit != null) {
                        try {
                            TorStream torStream = circuit.openDirectoryStream(OPEN_DIRECTORY_STREAM_TIMEOUT, true);
                            res.doComplete(torStream);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    failCount += 1;
                }
                res.doError(new OpenFailedException("Could not create circuit for directory stream"));
            }
        });
        return res;
    }

    @Override
    public Future<TorStream> openBridgeStream(final Router bridgeRouter) {
        final TypedFuture<TorStream> res = future();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                DirectoryCircuit circuit = tryOpenDirectoryCircuit(new DirectoryCircuitFactory(bridgeRouter, manager));
                if (circuit == null) {
                    res.doError(new OpenFailedException("Could not create directory circuit for path"));
                    return;
                }
                try {
                    TorStream torStream = circuit.openDirectoryStream(OPEN_DIRECTORY_STREAM_TIMEOUT, true);
                    res.doComplete(torStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    res.doError(e);
                }
            }
        });
        return res;
    }


    private DirectoryCircuit tryOpenDirectoryCircuit(DirectoryCircuitFactory factory) {
        final DirectoryCircuitResult result = new DirectoryCircuitResult();
        final CircuitCreationRequest req = new CircuitCreationRequest(factory, result);
        final CircuitBuildTask task = new CircuitBuildTask(req, manager.getConnectionCache());
        task.run();
        if (result.isSuccessful()) {
            return (DirectoryCircuit) task.getCreationRequest().getCircuit();
        } else {
            return null;
        }
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
