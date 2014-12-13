package im.actor.torlib;


import im.actor.torlib.connections.Connection;
import im.actor.torlib.errors.ConnectionFailedException;
import im.actor.torlib.errors.ConnectionHandshakeException;
import im.actor.torlib.errors.ConnectionTimeoutException;

public interface ConnectionCache {
	/**
	 * Returns a completed connection to the specified router.  If an open connection 
	 * to the requested router already exists it is returned, otherwise a new connection
	 * is opened. 
	 * 
	 * @param router The router to which a connection is requested.
	 * @param isDirectoryConnection Is this going to be used as a directory connection.
	 * @return a completed connection to the specified router.
	 * @throws InterruptedException if thread is interrupted while waiting for connection to complete.
	 * @throws im.actor.torlib.errors.ConnectionTimeoutException if timeout expires before connection completes.
	 * @throws im.actor.torlib.errors.ConnectionFailedException if connection fails due to I/O error
	 * @throws im.actor.torlib.errors.ConnectionHandshakeException if connection fails because an error occurred during handshake phase
	 */
	Connection getConnectionTo(Router router, boolean isDirectoryConnection) throws InterruptedException, ConnectionTimeoutException, ConnectionFailedException, ConnectionHandshakeException;
	
	void close();

	boolean isClosed();
}
