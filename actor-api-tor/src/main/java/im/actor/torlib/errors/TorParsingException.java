package im.actor.torlib.errors;


import im.actor.torlib.errors.TorException;

public class TorParsingException extends TorException {
	public TorParsingException(String string) {
		super(string);
	}

	public TorParsingException(String string, Throwable ex) {
		super(string, ex);
	}

	private static final long serialVersionUID = -4997757416476363399L;
}
