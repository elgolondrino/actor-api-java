package im.actor.torlib.circuits;

import java.util.List;
import java.util.concurrent.TimeoutException;

import im.actor.torlib.circuits.path.CircuitPathChooser;
import im.actor.torlib.circuits.path.PathSelectionFailedException;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.StreamConnectFailedException;

public class DirectoryCircuitImpl extends CircuitImpl implements DirectoryCircuit {

    public DirectoryCircuitImpl(List<Router> path, CircuitManager circuitManager) {
        super(path, circuitManager);
    }

    public TorStream openDirectoryStream(long timeout, boolean autoclose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        final TorStream torStream = createNewStream(autoclose);
        try {
            torStream.openDirectory(timeout);
            return torStream;
        } catch (Exception e) {
            removeStream(torStream);
            return processStreamOpenException(e);
        }
    }

    @Override
    protected String getCircuitTypeLabel() {
        return "Directory";
    }
}
