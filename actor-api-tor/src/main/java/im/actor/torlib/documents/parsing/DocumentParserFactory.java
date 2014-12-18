package im.actor.torlib.documents.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.KeyCertificateDocument;

public interface DocumentParserFactory {
	DocumentParser<DescriptorDocument> createRouterDescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificateDocument> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
