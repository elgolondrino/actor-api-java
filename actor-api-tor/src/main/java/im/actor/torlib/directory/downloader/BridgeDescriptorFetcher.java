package im.actor.torlib.directory.downloader;

import java.nio.ByteBuffer;

import im.actor.torlib.RouterDescriptor;
import im.actor.torlib.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<RouterDescriptor>{

	@Override
	String getRequestPath() {
		return "/tor/server/authority";
	}

	@Override
	DocumentParser<RouterDescriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterDescriptorParser(response, true);
	}
}
