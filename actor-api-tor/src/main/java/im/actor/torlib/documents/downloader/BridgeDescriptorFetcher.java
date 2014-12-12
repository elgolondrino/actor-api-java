package im.actor.torlib.documents.downloader;

import java.nio.ByteBuffer;

import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<DescriptorDocument> {

    @Override
    public String getRequestPath() {
        return "/tor/server/authority";
    }

    @Override
    public DocumentParser<DescriptorDocument> createParser(ByteBuffer response) {
        return PARSER_FACTORY.createRouterDescriptorParser(response);
    }
}
