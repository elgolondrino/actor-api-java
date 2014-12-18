package im.actor.torlib.documents.parsing;


public interface DocumentParser<T> {
	boolean parse(DocumentParsingResultHandler<T> resultHandler);
	DocumentParsingResult<T> parse();
}
