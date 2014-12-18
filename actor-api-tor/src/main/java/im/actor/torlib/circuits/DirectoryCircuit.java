package im.actor.torlib.circuits;

import java.util.List;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

public class DirectoryCircuit extends Circuit {

    public DirectoryCircuit(List<Router> path, Connection connection, CircuitManager circuitManager) {
        super(path, connection, circuitManager);
    }
}
