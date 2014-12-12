package im.actor.torlib;

import java.util.concurrent.TimeoutException;

import im.actor.torlib.circuits.TorStream;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.data.exitpolicy.ExitTarget;
import im.actor.torlib.errors.StreamConnectFailedException;

public interface ExitCircuit extends Circuit {
	
	/**
	 * Open an exit stream from the final node in this circuit to the 
	 * specified target address and port.
	 * 
	 * @param address The network address of the exit target.
	 * @param port The port of the exit target.
	 * @return The status response returned by trying to open the stream.
	 */
	TorStream openExitStream(IPv4Address address, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException;
	
	/**
	 * Open an exit stream from the final node in this circuit to the
	 * specified target hostname and port.
	 * 
	 * @param hostname The network hostname of the exit target.
	 * @param port The port of the exit target.
	 * @return The status response returned by trying to open the stream.
	 */
	TorStream openExitStream(String hostname, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException;
	
	/**
	 * Return true if the final node of this circuit is believed to be able to connect to
	 * the specified <code>ExitTarget</code>.  Returns false if the target destination is
	 * not permitted by the exit policy of the final node in this circuit or if the target
	 * has been previously recorded to have failed through this circuit.
	 * 
	 * @param target The exit destination.
	 * @return Return true if is likely that the final node of this circuit can connect to the specified exit target.
	 */
	boolean canHandleExitTo(ExitTarget target);

	boolean canHandleExitToPort(int port);
	/**
	 * Records the specified <code>ExitTarget</code> as a failed connection so that {@link #canHandleExitTo(ExitTarget)} will
	 * no longer return true for this exit destination.
	 * 
	 * @param target The <code>ExitTarget</code> to which a connection has failed through this circuit.
	 */
	public void recordFailedExitTarget(ExitTarget target);

}
