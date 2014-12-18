package im.actor.torlib.circuits;

import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import im.actor.torlib.circuits.build.*;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.connections.ConnectionCache;
import im.actor.torlib.TorConfig;
import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.utils.IPv4Address;
import im.actor.torlib.directory.NewDirectory;

public class CircuitManager {

    private final TorConfig config;
    private final NewDirectory directory;
    private final ConnectionCache connectionCache;
    private final CircuitPathChooser pathChooser;

    private final ExitActiveCircuits exitActiveCircuits;

    private final InternalCircuitsInt internalCircuits;
    private final ExitCircuitsInt exitCircuits;
    private final DirectoryCircuitsInt directoryCircuits;

    public CircuitManager(TorConfig config, NewDirectory directory, ConnectionCache connectionCache) {
        this.config = config;
        this.directory = directory;
        this.connectionCache = connectionCache;
        this.pathChooser = CircuitPathChooser.create(config, directory);
        this.exitActiveCircuits = new ExitActiveCircuits();

        this.exitCircuits = ExitCircuitsActor.get(this);
        this.internalCircuits = InternalCircuitsActor.get(this);
        this.directoryCircuits = DirectoryCircuitsActor.get(this);
    }

    public TorConfig getConfig() {
        return config;
    }

    public NewDirectory getDirectory() {
        return directory;
    }

    @Deprecated
    public ConnectionCache getConnectionCache() {
        return connectionCache;
    }

    @Deprecated
    public CircuitPathChooser getPathChooser() {
        return pathChooser;
    }

    @Deprecated
    public ExitActiveCircuits getExitActiveCircuits() {
        return exitActiveCircuits;
    }

    public Future<TorStream> openDirectoryStream() {
        return directoryCircuits.openDirectoryStream();
    }

    public Future<TorStream> openExitStreamTo(String hostname, int port) {
        return exitCircuits.openExitStream(hostname, port, 15000);
    }

    public Future<TorStream> openExitStreamTo(IPv4Address address, int port) {
        return exitCircuits.openExitStream(address, port, 15000);
    }

    public Circuit pickInternalCircuit() throws InterruptedException {
        final Future<Circuit> future = internalCircuits.pickInternalCircuit();
        final Circuit[] res = new Circuit[1];
        future.addListener(new FutureCallback<Circuit>() {
            @Override
            public void onResult(Circuit result) {
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

    public void startBuildingCircuits() {
        exitCircuits.start();
        internalCircuits.start();
    }

    public void stopBuildingCircuits() {
        exitCircuits.stop();
        internalCircuits.stop();
    }
}
