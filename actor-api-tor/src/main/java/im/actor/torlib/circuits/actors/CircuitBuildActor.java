package im.actor.torlib.circuits.actors;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.actors.extender.CircuitExtender;
import im.actor.torlib.circuits.actors.path.CircuitFactory;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.connections.ConnectionImpl;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.log.Log;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 18.12.14.
 */
public class CircuitBuildActor extends TaskActor<im.actor.torlib.circuits.Circuit> {

    private static AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static ActorRef build(final CircuitFactory factory, final ConnectionCache connectionCache) {
        final int id = NEXT_ID.getAndIncrement();
        return ActorSystem.system().actorOf(Props.create(CircuitBuildActor.class, new ActorCreator<CircuitBuildActor>() {
            @Override
            public CircuitBuildActor create() {
                return new CircuitBuildActor(factory, connectionCache, id);
            }
        }), "/tor/circuit/build/" + id);
    }

    private static Executor EXECUTOR = Executors.newCachedThreadPool();

    private final String TAG;
    private final int ID;

    private final CircuitFactory factory;
    private final ConnectionCache connectionCache;

    public CircuitBuildActor(CircuitFactory factory, ConnectionCache connectionCache, int id) {
        this.factory = factory;
        this.connectionCache = connectionCache;
        this.ID = id;
        this.TAG = "CircuitBuild#" + id;
    }

    @Override
    public void startTask() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Building path...");
                    List<Router> path = factory.buildNewPath();
                    if (path == null || path.size() == 0) {
                        error(new Exception("Unable to build path"));
                        return;
                    }

                    Router firstRouter = path.get(0);

                    Log.d(TAG, "Opening connection...");
                    ConnectionImpl connection;
                    try {
                        connection = connectionCache.getConnectionTo(firstRouter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error(new ConnectException());
                        return;
                    }

                    Circuit circuit = factory.buildNewCircuit(path, connection);

                    Log.d(TAG, "Building cell sequence...");
                    CircuitExtender extender = new CircuitExtender(circuit);

                    extender.createFastTo(firstRouter);
                    for (int i = 1; i < path.size(); i++) {
                        extender.extendTo(path.get(i));
                    }

                    Log.d(TAG, "Completed");
                    complete(circuit);
                } catch (Exception e) {
                    e.printStackTrace();
                    error(e);
                }
            }
        });
    }
}
