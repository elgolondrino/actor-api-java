package im.actor.torlib;

public class ConnectionHandshakeException extends ConnectionIOException {
	
	private static final long serialVersionUID = -2544633445932967966L;
	
	public ConnectionHandshakeException(String message) {
		super(message);
	}
}
