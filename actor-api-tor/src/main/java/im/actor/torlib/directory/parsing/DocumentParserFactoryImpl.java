package im.actor.torlib.directory.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.directory.ConsensusDocument;
import im.actor.torlib.Descriptor;
import im.actor.torlib.KeyCertificate;
import im.actor.torlib.directory.certificate.KeyCertificateParser;
import im.actor.torlib.directory.consensus.ConsensusDocumentParser;
import im.actor.torlib.directory.router.RouterMicrodescriptorParser;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<Descriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterMicrodescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
