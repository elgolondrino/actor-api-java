package im.actor.torlib.circuits.build;

import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.circuits.TorStream;
import im.actor.utils.IPv4Address;

/**
 * Created by ex3ndr on 15.12.14.
 */
public interface CircuitCreationInt {
    public void start();

    public Future<TorStream> openExitStream(String hostname, int port, long timeout);

    public Future<TorStream> openExitStream(IPv4Address address, int port, long timeout);

    public void stop();
}
