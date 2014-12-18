package im.actor.torlib.documents.parsing.router;

public enum RouterDescriptorKeyword {
	ONION_KEY("onion-key", 0),
	NTOR_ONION_KEY("ntor-onion-key", 1),
	A("a", 1),
	FAMILY("family"),
	P("p", 2),
	UNKNOWN_KEYWORD("KEYWORD NOT FOUNE");
	
	public final static int VARIABLE_ARGUMENT_COUNT = -1;
	
	private final String keyword;
	private final int argumentCount;
	
	RouterDescriptorKeyword(String keyword) {
		this(keyword, VARIABLE_ARGUMENT_COUNT);
	}

	RouterDescriptorKeyword(String keyword, int argumentCount) {
		this.keyword = keyword;
		this.argumentCount = argumentCount;
	}
	
	String getKeyword() {
		return keyword;
	}
	
	int getArgumentCount() {
		return argumentCount;
	}
	
	static RouterDescriptorKeyword findKeyword(String keyword) {
		for(RouterDescriptorKeyword k: values()) {
			if(k.getKeyword().equals(keyword)) {
				return k;
			}
		}
		return UNKNOWN_KEYWORD;
	}
}
