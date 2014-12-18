package im.actor.torlib.circuits.actors;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.errors.StreamConnectFailedException;
import im.actor.utils.IPv4Address;

import java.util.concurrent.TimeoutException;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class CircuitStreamFactory {
    public static TorStream openDirectoryStream(Circuit circuit, long timeout, boolean autoclose)
            throws InterruptedException, StreamConnectFailedException, TimeoutException {
        TorStream stream = circuit.createNewStream(autoclose);
        try {
            stream.openDirectory(timeout);
            return stream;
        } catch (Exception e) {
            circuit.removeStream(stream);
            processStreamOpenException(e);
        }
        return stream;
    }

    public static TorStream openExitStream(Circuit circuit, IPv4Address address, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        return openExitStream(circuit, address.toString(), port, timeout);
    }

    public static TorStream openExitStream(Circuit circuit, String target, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        final TorStream torStream = circuit.createNewStream();
        try {
            torStream.openExit(target, port, timeout);
            return torStream;
        } catch (Exception e) {
            circuit.removeStream(torStream);
            return processStreamOpenException(e);
        }
    }

    protected static TorStream processStreamOpenException(Exception e) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        if (e instanceof InterruptedException) {
            throw (InterruptedException) e;
        } else if (e instanceof TimeoutException) {
            throw (TimeoutException) e;
        } else if (e instanceof StreamConnectFailedException) {
            throw (StreamConnectFailedException) e;
        } else {
            throw new IllegalStateException();
        }
    }

    public static TorStream openHiddenServiceStream(Circuit circuit, int port, long timeout)
            throws InterruptedException, TimeoutException, StreamConnectFailedException {
        final TorStream torStream = circuit.createNewStream();
        try {
            torStream.openExit("", port, timeout);
            return torStream;
        } catch (Exception e) {
            circuit.removeStream(torStream);
            return processStreamOpenException(e);
        }
    }
}
