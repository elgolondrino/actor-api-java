package im.actor.torlib.circuits.build.path;

import im.actor.torlib.circuits.CircuitImpl;

/**
 * Created by ex3ndr on 15.12.14.
 */
public abstract class CircuitFactory<T extends CircuitImpl> {
    public abstract T buildNewCircuit();
}
