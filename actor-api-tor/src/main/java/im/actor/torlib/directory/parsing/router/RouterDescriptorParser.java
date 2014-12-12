package im.actor.torlib.directory.parsing.router;

import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.errors.TorParsingException;
import im.actor.torlib.crypto.TorMessageDigest;
import im.actor.torlib.directory.parsing.BasicDocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParsingHandler;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentParsingResultHandler;

public class RouterDescriptorParser implements DocumentParser<DescriptorDocument> {

	
	private final DocumentFieldParser fieldParser;
	
	private DescriptorDocument currentDescriptorDocument;
	private DocumentParsingResultHandler<DescriptorDocument> resultHandler;
	
	public RouterDescriptorParser(DocumentFieldParser fieldParser) {
		this.fieldParser = fieldParser;
		this.fieldParser.setHandler(createParsingHandler());
	}

	private DocumentParsingHandler createParsingHandler() {
		return new DocumentParsingHandler() {
			public void parseKeywordLine() {
				processKeywordLine();
			}
			public void endOfDocument() { 
				if(currentDescriptorDocument != null) {
					finalizeDescriptor(currentDescriptorDocument);
				}
			}
		};
	}
	
	public boolean parse(DocumentParsingResultHandler<DescriptorDocument> resultHandler) {
		this.resultHandler = resultHandler;
		try {
			fieldParser.processDocument();
			return true;
		} catch(TorParsingException e) {
			resultHandler.parsingError(e.getMessage());
			return false;
		}
	}

	public DocumentParsingResult<DescriptorDocument> parse() {
		final BasicDocumentParsingResult<DescriptorDocument> result = new BasicDocumentParsingResult<DescriptorDocument>();
		parse(result);
		return result;
	}

	private void processKeywordLine() {
		final RouterDescriptorKeyword keyword = RouterDescriptorKeyword.findKeyword(fieldParser.getCurrentKeyword());
		if(!keyword.equals(RouterDescriptorKeyword.UNKNOWN_KEYWORD)) {
			processKeyword(keyword);
		}
		if(currentDescriptorDocument != null) {
			currentDescriptorDocument.setRawDocumentData(fieldParser.getRawDocument());
		}

	}
	

	private void processKeyword(RouterDescriptorKeyword keyword) {
		fieldParser.verifyExpectedArgumentCount(keyword.getKeyword(), keyword.getArgumentCount());
		switch(keyword) {
		case ONION_KEY:
			processOnionKeyLine();
			break;
			
		case NTOR_ONION_KEY:
			if(currentDescriptorDocument != null) {
				currentDescriptorDocument.setNtorOnionKey(fieldParser.parseNtorPublicKey());
			}
			break;
			
		case FAMILY:
			while(fieldParser.argumentsRemaining() > 0 && currentDescriptorDocument != null) {
				currentDescriptorDocument.addFamilyMember(fieldParser.parseString());
			}
			break;
		
		case P:
			processP();
			break;
	
		case A:
		default:
			break;
		}
	}
	
	private void processOnionKeyLine() {
		if(currentDescriptorDocument != null) {
			finalizeDescriptor(currentDescriptorDocument);
		}
		currentDescriptorDocument = new DescriptorDocument();
		fieldParser.resetRawDocument(RouterDescriptorKeyword.ONION_KEY.getKeyword() + "\n");
		currentDescriptorDocument.setOnionKey(fieldParser.parsePublicKey());
	}

	private void finalizeDescriptor(DescriptorDocument descriptorDocument) {
		final TorMessageDigest digest = new TorMessageDigest(true);
		digest.update(descriptorDocument.getRawDocumentData());
		descriptorDocument.setDescriptorDigest(digest.getHexDigest());
		if(!descriptorDocument.isValidDocument()) {
			resultHandler.documentInvalid(descriptorDocument, "Microdescriptor data invalid");
		} else {
			resultHandler.documentParsed(descriptorDocument);
		}
	}

	private void processP() {
		if(currentDescriptorDocument == null) {
			return;
		}
		final String ruleType = fieldParser.parseString();
		if("accept".equals(ruleType)) {
			currentDescriptorDocument.addAcceptPorts(fieldParser.parseString());
		} else if("reject".equals(ruleType)) {
			currentDescriptorDocument.addRejectPorts(fieldParser.parseString());
		} else {
			fieldParser.logWarn("Unexpected P field in microdescriptor: "+ ruleType);
		}
	}
}
