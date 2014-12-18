package im.actor.torlib.documents.parsing.consensus;

import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.errors.TorParsingException;
import im.actor.torlib.documents.parsing.BasicDocumentParsingResult;
import im.actor.torlib.documents.parsing.DocumentFieldParser;
import im.actor.torlib.documents.parsing.DocumentParser;
import im.actor.torlib.documents.parsing.DocumentParsingHandler;
import im.actor.torlib.documents.parsing.DocumentParsingResult;
import im.actor.torlib.documents.parsing.DocumentParsingResultHandler;

public class ConsensusDocumentParser implements DocumentParser<ConsensusDocument> {
	public enum DocumentSection { NO_SECTION, PREAMBLE, AUTHORITY, ROUTER_STATUS, FOOTER };

	// dir-spec.txt 3.2 
	// Unlike other formats described above, a SP in these documents must be a
	// single space character (hex 20).
	private final static String ITEM_DELIMITER = " ";
	
	private final PreambleSectionParser preambleParser;
	private final AuthoritySectionParser authorityParser;
	private final RouterStatusSectionParser routerStatusParser;
	private final FooterSectionParser footerParser;
	private final DocumentFieldParser fieldParser;
	private DocumentSection currentSection = DocumentSection.PREAMBLE;
	private final ConsensusDocument document;
	
	private DocumentParsingResultHandler<ConsensusDocument> resultHandler;
	
	public ConsensusDocumentParser(DocumentFieldParser fieldParser) {
		this.fieldParser = fieldParser;
		initializeParser();
		
		document = new ConsensusDocument();
		preambleParser = new PreambleSectionParser(fieldParser, document);
		authorityParser = new AuthoritySectionParser(fieldParser, document);
		routerStatusParser = new RouterStatusSectionParser(fieldParser, document);
		footerParser = new FooterSectionParser(fieldParser, document);
	}
	
	private void initializeParser() {
		fieldParser.resetRawDocument();
		fieldParser.setHandler(createParsingHandler());
		fieldParser.setDelimiter(ITEM_DELIMITER);
		fieldParser.setSignatureIgnoreToken("directory-signature");
		fieldParser.startSignedEntity();
	}
	
	public boolean parse(DocumentParsingResultHandler<ConsensusDocument> resultHandler) {
		this.resultHandler = resultHandler;
		try {
			fieldParser.processDocument();
			return true;
		} catch(TorParsingException e) {
			resultHandler.parsingError(e.getMessage());
			return false;
		}
	}
	
	public DocumentParsingResult<ConsensusDocument> parse() {
		final BasicDocumentParsingResult<ConsensusDocument> result = new BasicDocumentParsingResult<ConsensusDocument>();
		parse(result);
		return result;
	}

	private DocumentParsingHandler createParsingHandler() {
		return new DocumentParsingHandler() {

			public void endOfDocument() {
				document.setRawDocumentData(fieldParser.getRawDocument());
				resultHandler.documentParsed(document);
				fieldParser.logDebug("Finished parsing status document.");				
			}
			public void parseKeywordLine() {
				processKeywordLine();	
			}
			
		};
	}
	private void processKeywordLine() {
		DocumentSection newSection = null;
		while(currentSection != DocumentSection.NO_SECTION) {
			switch(currentSection) {
			case PREAMBLE:
				newSection = preambleParser.parseKeywordLine();
				break;
			case AUTHORITY:
				newSection = authorityParser.parseKeywordLine();
				break;
			case ROUTER_STATUS:
				newSection = routerStatusParser.parseKeywordLine();
				break;
			case FOOTER:
				newSection = footerParser.parseKeywordLine();
				break;
			default:
				break;
			}
			if(newSection == currentSection)
				return;
			
			currentSection = newSection;
		}	
	}
	
}