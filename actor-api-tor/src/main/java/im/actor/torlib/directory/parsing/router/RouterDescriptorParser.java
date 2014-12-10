package im.actor.torlib.directory.parsing.router;

import im.actor.torlib.directory.Descriptor;
import im.actor.torlib.TorParsingException;
import im.actor.torlib.crypto.TorMessageDigest;
import im.actor.torlib.directory.parsing.BasicDocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParsingHandler;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentParsingResultHandler;

public class RouterDescriptorParser implements DocumentParser<Descriptor> {

	
	private final DocumentFieldParser fieldParser;
	
	private Descriptor currentDescriptor;
	private DocumentParsingResultHandler<Descriptor> resultHandler;
	
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
				if(currentDescriptor != null) {
					finalizeDescriptor(currentDescriptor);
				}
			}
		};
	}
	
	public boolean parse(DocumentParsingResultHandler<Descriptor> resultHandler) {
		this.resultHandler = resultHandler;
		try {
			fieldParser.processDocument();
			return true;
		} catch(TorParsingException e) {
			resultHandler.parsingError(e.getMessage());
			return false;
		}
	}

	public DocumentParsingResult<Descriptor> parse() {
		final BasicDocumentParsingResult<Descriptor> result = new BasicDocumentParsingResult<Descriptor>();
		parse(result);
		return result;
	}

	private void processKeywordLine() {
		final RouterDescriptorKeyword keyword = RouterDescriptorKeyword.findKeyword(fieldParser.getCurrentKeyword());
		if(!keyword.equals(RouterDescriptorKeyword.UNKNOWN_KEYWORD)) {
			processKeyword(keyword);
		}
		if(currentDescriptor != null) {
			currentDescriptor.setRawDocumentData(fieldParser.getRawDocument());
		}

	}
	

	private void processKeyword(RouterDescriptorKeyword keyword) {
		fieldParser.verifyExpectedArgumentCount(keyword.getKeyword(), keyword.getArgumentCount());
		switch(keyword) {
		case ONION_KEY:
			processOnionKeyLine();
			break;
			
		case NTOR_ONION_KEY:
			if(currentDescriptor != null) {
				currentDescriptor.setNtorOnionKey(fieldParser.parseNtorPublicKey());
			}
			break;
			
		case FAMILY:
			while(fieldParser.argumentsRemaining() > 0 && currentDescriptor != null) {
				currentDescriptor.addFamilyMember(fieldParser.parseString());
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
		if(currentDescriptor != null) {
			finalizeDescriptor(currentDescriptor);
		}
		currentDescriptor = new Descriptor();
		fieldParser.resetRawDocument(RouterDescriptorKeyword.ONION_KEY.getKeyword() + "\n");
		currentDescriptor.setOnionKey(fieldParser.parsePublicKey());
	}

	private void finalizeDescriptor(Descriptor descriptor) {
		final TorMessageDigest digest = new TorMessageDigest(true);
		digest.update(descriptor.getRawDocumentData());
		descriptor.setDescriptorDigest(digest.getHexDigest());
		if(!descriptor.isValidDocument()) {
			resultHandler.documentInvalid(descriptor, "Microdescriptor data invalid");
		} else {
			resultHandler.documentParsed(descriptor);
		}
	}

	private void processP() {
		if(currentDescriptor == null) {
			return;
		}
		final String ruleType = fieldParser.parseString();
		if("accept".equals(ruleType)) {
			currentDescriptor.addAcceptPorts(fieldParser.parseString());
		} else if("reject".equals(ruleType)) {
			currentDescriptor.addRejectPorts(fieldParser.parseString());
		} else {
			fieldParser.logWarn("Unexpected P field in microdescriptor: "+ ruleType);
		}
	}
}
