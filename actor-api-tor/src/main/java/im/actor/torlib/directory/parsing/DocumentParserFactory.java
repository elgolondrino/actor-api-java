package im.actor.torlib.directory.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.ConsensusDocument;
import im.actor.torlib.KeyCertificate;
import im.actor.torlib.RouterDescriptor;
import im.actor.torlib.RouterMicrodescriptor;

public interface DocumentParserFactory {
	DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures);
	
	DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
