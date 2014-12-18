package im.actor.torlib.circuits.actors.path;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.directory.routers.Router;

import java.util.List;

/**
 * Created by ex3ndr on 15.12.14.
 */
public abstract class CircuitFactory<T extends Circuit> {
    public abstract List<Router> buildNewPath();

    public abstract T buildNewCircuit(List<Router> path, Connection connection);
}
