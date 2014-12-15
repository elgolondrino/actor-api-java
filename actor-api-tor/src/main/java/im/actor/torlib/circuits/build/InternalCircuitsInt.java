package im.actor.torlib.circuits.build;

import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.circuits.InternalCircuit;

/**
 * Created by ex3ndr on 15.12.14.
 */
public interface InternalCircuitsInt {
    public void start();

    public Future<InternalCircuit> pickInternalCircuit();

    public void stop();
}
