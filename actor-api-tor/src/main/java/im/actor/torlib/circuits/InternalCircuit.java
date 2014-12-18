package im.actor.torlib.circuits;

import java.util.List;

import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

public class InternalCircuit extends Circuit {

    public InternalCircuit(List<Router> path, Connection connection, CircuitManager circuitManager) {
        super(path, connection, circuitManager);
    }
}
