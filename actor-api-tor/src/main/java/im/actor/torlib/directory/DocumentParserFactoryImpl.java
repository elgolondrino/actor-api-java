package im.actor.torlib.directory;

import java.nio.ByteBuffer;

import im.actor.torlib.ConsensusDocument;
import im.actor.torlib.KeyCertificate;
import im.actor.torlib.RouterDescriptor;
import im.actor.torlib.RouterMicrodescriptor;
import im.actor.torlib.directory.certificate.KeyCertificateParser;
import im.actor.torlib.directory.consensus.ConsensusDocumentParser;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParserFactory;
import im.actor.torlib.directory.router.RouterDescriptorParser;
import im.actor.torlib.directory.router.RouterMicrodescriptorParser;
import im.actor.torlib.ConsensusDocument;
import im.actor.torlib.RouterMicrodescriptor;
import im.actor.torlib.directory.certificate.KeyCertificateParser;
import im.actor.torlib.directory.consensus.ConsensusDocumentParser;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParserFactory;
import im.actor.torlib.directory.router.RouterDescriptorParser;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures) {
		return new RouterDescriptorParser(new DocumentFieldParserImpl(buffer), verifySignatures);
	}

	public DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterMicrodescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
