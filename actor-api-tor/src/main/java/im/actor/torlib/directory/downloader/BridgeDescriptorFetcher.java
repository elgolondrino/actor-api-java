package im.actor.torlib.directory.downloader;

import java.nio.ByteBuffer;

import im.actor.torlib.Descriptor;
import im.actor.torlib.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<Descriptor> {

    @Override
    public String getRequestPath() {
        return "/tor/server/authority";
    }

    @Override
    public DocumentParser<Descriptor> createParser(ByteBuffer response) {
        return PARSER_FACTORY.createRouterMicrodescriptorParser(response);
    }
}
