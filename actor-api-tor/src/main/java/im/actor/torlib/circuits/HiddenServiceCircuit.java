package im.actor.torlib.circuits;

import im.actor.torlib.errors.StreamConnectFailedException;

import java.util.concurrent.TimeoutException;


public interface HiddenServiceCircuit extends Circuit {
	TorStream openStream(int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException;
}
