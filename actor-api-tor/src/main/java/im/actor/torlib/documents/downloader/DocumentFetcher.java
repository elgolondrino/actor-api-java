package im.actor.torlib.documents.downloader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import im.actor.torlib.documents.parsing.DocumentParserFactoryImpl;
import im.actor.torlib.documents.parsing.BasicDocumentParsingResult;
import im.actor.torlib.documents.parsing.DocumentParser;
import im.actor.torlib.documents.parsing.DocumentParserFactory;
import im.actor.torlib.errors.DirectoryRequestFailedException;

public abstract class DocumentFetcher<T> {
    protected final static DocumentParserFactory PARSER_FACTORY = new DocumentParserFactoryImpl();


    public abstract String getRequestPath();

    public abstract DocumentParser<T> createParser(ByteBuffer response);

    public List<T> requestDocuments(TorHttpConnection torHttpConnection) throws IOException, DirectoryRequestFailedException {
        final ByteBuffer body = makeRequest(torHttpConnection);
        if (body.hasRemaining()) {
            return processResponse(body);
        } else {
            return Collections.emptyList();
        }
    }

    private ByteBuffer makeRequest(TorHttpConnection torHttpConnection) throws IOException, DirectoryRequestFailedException {

        torHttpConnection.sendGetRequest(getRequestPath());
        torHttpConnection.readResponse();
        if (torHttpConnection.getStatusCode() == 200) {
            return torHttpConnection.getMessageBody();
        }

        throw new DirectoryRequestFailedException("Request " + getRequestPath() + " to directory " +
                torHttpConnection.getHost() + " returned error code: " +
                torHttpConnection.getStatusCode() + " " + torHttpConnection.getStatusMessage());

    }

    private List<T> processResponse(ByteBuffer response) throws DirectoryRequestFailedException {
        final DocumentParser<T> parser = createParser(response);
        final BasicDocumentParsingResult<T> result = new BasicDocumentParsingResult<T>();
        final boolean success = parser.parse(result);
        if (success) {
            return result.getParsedDocuments();
        }
        throw new DirectoryRequestFailedException("Failed to parse response from directory: " + result.getMessage());
    }
}
