package im.actor.torlib.directory.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.directory.ConsensusDocument;
import im.actor.torlib.directory.Descriptor;
import im.actor.torlib.directory.KeyCertificate;

public interface DocumentParserFactory {
	DocumentParser<Descriptor> createRouterMicrodescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
