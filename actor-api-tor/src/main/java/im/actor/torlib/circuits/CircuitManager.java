package im.actor.torlib.circuits;

import java.util.*;

import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import im.actor.torlib.circuits.build.*;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.connections.Connection;
import im.actor.utils.IPv4Address;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.errors.OpenFailedException;

public class CircuitManager {

    private final static int OPEN_DIRECTORY_STREAM_RETRY_COUNT = 5;

    private final TorConfig config;
    private final NewDirectory directory;
    private final ConnectionCache connectionCache;
    private final CircuitPathChooser pathChooser;

    private final ActiveCircuits activeCircuits;

    private final InternalCircuitsInt circuitsInt;
    private final CircuitCreationInt circuitCreationActor;

    public CircuitManager(TorConfig config, NewDirectory directory, ConnectionCache connectionCache) {
        this.config = config;
        this.directory = directory;
        this.connectionCache = connectionCache;
        this.pathChooser = CircuitPathChooser.create(config, directory);
        this.activeCircuits = new ActiveCircuits();

        this.circuitCreationActor = CircuitCreationActor.get(this);
        this.circuitsInt = InternalCircuitsActor.get(this);
    }

    public TorConfig getConfig() {
        return config;
    }

    public NewDirectory getDirectory() {
        return directory;
    }

    public ConnectionCache getConnectionCache() {
        return connectionCache;
    }

    public CircuitPathChooser getPathChooser() {
        return pathChooser;
    }

    public ActiveCircuits getActiveCircuits() {
        return activeCircuits;
    }

    public void startBuildingCircuits() {
        circuitCreationActor.start();
        circuitsInt.start();
    }

    public void stopBuildingCircuits() {
        circuitCreationActor.stop();
        circuitsInt.stop();
    }


    public Future<TorStream> openExitStreamTo(String hostname, int port) {
        return circuitCreationActor.openExitStream(hostname, port, 15000);
    }

    public Future<TorStream> openExitStreamTo(IPv4Address address, int port) {
        return circuitCreationActor.openExitStream(address, port, 15000);
    }

    public InternalCircuit pickInternalCircuit() throws InterruptedException {
        final Future<InternalCircuit> future = circuitsInt.pickInternalCircuit();
        final InternalCircuit[] res = new InternalCircuit[1];
        future.addListener(new FutureCallback<InternalCircuit>() {
            @Override
            public void onResult(InternalCircuit result) {
                synchronized (res) {
                    res[0] = result;
                    res.notifyAll();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                synchronized (res) {
                    res[0] = null;
                    res.notifyAll();
                }
            }
        });
        synchronized (res) {
            if (res[0] != null) {
                return res[0];
            }
            res.wait();
        }

        if (res[0] == null) {
            throw new InterruptedException();
        }
        return res[0];
    }

    // Obsolete directory circuits

    @Deprecated
    public DirectoryCircuit openDirectoryCircuit() throws OpenFailedException {
        int failCount = 0;
        while (failCount < OPEN_DIRECTORY_STREAM_RETRY_COUNT) {
            final DirectoryCircuit circuit = new DirectoryCircuitImpl(this, null);
            if (tryOpenDirectoryCircuit(circuit)) {
                return circuit;
            }
            failCount += 1;
        }
        throw new OpenFailedException("Could not create circuit for directory stream");
    }

    @Deprecated
    public DirectoryCircuit openDirectoryCircuitTo(Router destRouter) throws OpenFailedException {
        final DirectoryCircuit circuit = new DirectoryCircuitImpl(this, Arrays.asList(destRouter));
        if (!tryOpenDirectoryCircuit(circuit)) {
            throw new OpenFailedException("Could not create directory circuit for path");
        }
        return circuit;
    }

    @Deprecated
    private boolean tryOpenDirectoryCircuit(Circuit circuit) {
        final DirectoryCircuitResult result = new DirectoryCircuitResult();
        final CircuitCreationRequest req = new CircuitCreationRequest(pathChooser, circuit, result);
        final CircuitBuildTask task = new CircuitBuildTask(req, connectionCache);
        task.run();
        return result.isSuccessful();
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
