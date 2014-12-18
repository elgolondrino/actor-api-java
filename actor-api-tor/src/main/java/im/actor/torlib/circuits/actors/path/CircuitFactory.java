package im.actor.torlib.circuits.actors.path;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.connections.ConnectionImpl;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public abstract class CircuitFactory {
    public abstract List<Router> buildNewPath();

    public abstract Circuit buildNewCircuit(List<Router> path, ConnectionImpl connection);
}
