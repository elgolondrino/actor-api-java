package im.actor.torlib.directory.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.directory.ConsensusDocument;
import im.actor.torlib.directory.Descriptor;
import im.actor.torlib.directory.KeyCertificate;
import im.actor.torlib.directory.parsing.certificate.KeyCertificateParser;
import im.actor.torlib.directory.parsing.consensus.ConsensusDocumentParser;
import im.actor.torlib.directory.parsing.router.RouterDescriptorParser;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<Descriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterDescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
