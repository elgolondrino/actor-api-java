package im.actor.torlib.documents.parsing;

import java.util.List;

public interface DocumentParsingResult<T> {
	T getDocument();
	List<T> getParsedDocuments();
	boolean isOkay();
	boolean isInvalid();
	T getInvalidDocument();
	boolean isError();
	String getMessage();
}
