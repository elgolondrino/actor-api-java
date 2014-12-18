package im.actor.torlib.circuits.build;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.build.extender.CircuitExtender;
import im.actor.torlib.circuits.build.path.CircuitFactory;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.directory.routers.Router;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 18.12.14.
 */
public class CircuitBuildActor<T extends Circuit> extends TaskActor<T> {

    private static AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static ActorRef build(final CircuitFactory factory, final ConnectionCache connectionCache) {
        return ActorSystem.system().actorOf(Props.create(CircuitBuildActor.class, new ActorCreator<CircuitBuildActor>() {
            @Override
            public CircuitBuildActor create() {
                return new CircuitBuildActor(factory, connectionCache);
            }
        }), "/tor/circuit/build/" + NEXT_ID.getAndIncrement());
    }

    private static Executor EXECUTOR = Executors.newCachedThreadPool();

    private CircuitFactory<T> factory;
    private ConnectionCache connectionCache;

    public CircuitBuildActor(CircuitFactory<T> factory, ConnectionCache connectionCache) {
        this.factory = factory;
        this.connectionCache = connectionCache;
    }

    @Override
    public void startTask() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Router> path = factory.buildNewPath();
                    if (path == null || path.size() == 0) {
                        error(new Exception("Unable to build path"));
                        return;
                    }

                    Router firstRouter = path.get(0);

                    Connection connection;
                    try {
                        connection = connectionCache.getConnectionTo(firstRouter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error(new ConnectException());
                        return;
                    }

                    T circuit = factory.buildNewCircuit(path, connection);

                    CircuitExtender extender = new CircuitExtender(circuit);

                    extender.createFastTo(firstRouter);
                    for (int i = 1; i < path.size(); i++) {
                        extender.extendTo(path.get(i));
                    }

                    complete(circuit);
                } catch (Exception e) {
                    e.printStackTrace();
                    error(e);
                }
            }
        });
    }
}
