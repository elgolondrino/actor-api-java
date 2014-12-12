package im.actor.torlib.errors;

public class ConnectionHandshakeException extends ConnectionIOException {
	
	private static final long serialVersionUID = -2544633445932967966L;
	
	public ConnectionHandshakeException(String message) {
		super(message);
	}
}
