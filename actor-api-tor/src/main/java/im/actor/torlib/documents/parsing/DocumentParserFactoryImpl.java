package im.actor.torlib.documents.parsing;

import java.nio.ByteBuffer;

import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.KeyCertificateDocument;
import im.actor.torlib.documents.parsing.certificate.KeyCertificateParser;
import im.actor.torlib.documents.parsing.consensus.ConsensusDocumentParser;
import im.actor.torlib.documents.parsing.router.RouterDescriptorParser;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificateDocument> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<DescriptorDocument> createRouterDescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterDescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
