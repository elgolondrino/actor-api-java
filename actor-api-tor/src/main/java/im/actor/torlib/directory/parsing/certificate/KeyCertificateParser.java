package im.actor.torlib.directory.parsing.certificate;

import im.actor.torlib.documents.KeyCertificateDocument;
import im.actor.torlib.errors.TorParsingException;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.crypto.TorSignature;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.directory.parsing.BasicDocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParsingHandler;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.torlib.directory.parsing.DocumentParsingResultHandler;

public class KeyCertificateParser implements DocumentParser<KeyCertificateDocument> {
	private final static int CURRENT_CERTIFICATE_VERSION = 3;
	private final DocumentFieldParser fieldParser;
	private KeyCertificateDocument currentCertificate;
	private DocumentParsingResultHandler<KeyCertificateDocument> resultHandler;
	
	public KeyCertificateParser(DocumentFieldParser fieldParser) {
		this.fieldParser = fieldParser;
		this.fieldParser.setHandler(createParsingHandler());
	}
	
	private DocumentParsingHandler createParsingHandler() {
		return new DocumentParsingHandler() {
			public void parseKeywordLine() {
				processKeywordLine();
			}
			
			public void endOfDocument() {
			}
		};
	}
	
	private void processKeywordLine() {
		final KeyCertificateKeyword keyword = KeyCertificateKeyword.findKeyword(fieldParser.getCurrentKeyword());
		/*
		 * dirspec.txt (1.2)
		 * When interpreting a Document, software MUST ignore any KeywordLine that
		 * starts with a keyword it doesn't recognize;
		 */
		if(!keyword.equals(KeyCertificateKeyword.UNKNOWN_KEYWORD))
			processKeyword(keyword);
	}
	
	private void startNewCertificate() {
		fieldParser.resetRawDocument();
		fieldParser.startSignedEntity();
		currentCertificate = new KeyCertificateDocument();
	}
	
	public boolean parse(DocumentParsingResultHandler<KeyCertificateDocument> resultHandler) {
		this.resultHandler = resultHandler;
		startNewCertificate();
		try {
			fieldParser.processDocument();
			return true;
		} catch(TorParsingException e) {
			resultHandler.parsingError(e.getMessage());
			return false;
		}
	}
	
	public DocumentParsingResult<KeyCertificateDocument> parse() {
		final BasicDocumentParsingResult<KeyCertificateDocument> result = new BasicDocumentParsingResult<KeyCertificateDocument>();
		parse(result);
		return result;
	}

	private void processKeyword(KeyCertificateKeyword keyword) {
		switch(keyword) {
		case DIR_KEY_CERTIFICATE_VERSION:
			processCertificateVersion();
			break;
		case DIR_ADDRESS:
			processDirectoryAddress();
			break;
		case FINGERPRINT:
			currentCertificate.setAuthorityFingerprint(fieldParser.parseHexDigest());
			break;
		case DIR_IDENTITY_KEY:
			currentCertificate.setAuthorityIdentityKey(fieldParser.parsePublicKey());
			break;
		case DIR_SIGNING_KEY:
			currentCertificate.setAuthoritySigningKey(fieldParser.parsePublicKey());
			break;
		case DIR_KEY_PUBLISHED:
			currentCertificate.setKeyPublishedTime(fieldParser.parseTimestamp());
			break;
		case DIR_KEY_EXPIRES:
			currentCertificate.setKeyExpiryTime(fieldParser.parseTimestamp());
			break;
		case DIR_KEY_CROSSCERT:
			verifyCrossSignature(fieldParser.parseSignature());
			break;
		case DIR_KEY_CERTIFICATION:
			processCertificateSignature();
			break;
		case UNKNOWN_KEYWORD:
			break;
		}
	}
	
	private void processCertificateVersion() {
		final int version = fieldParser.parseInteger();
		if(version != CURRENT_CERTIFICATE_VERSION)
			throw new TorParsingException("Unexpected certificate version: " + version);
	}
	
	private void processDirectoryAddress() {
		final String addrport = fieldParser.parseString();
		final String[] args = addrport.split(":");
		if(args.length != 2)
			throw new TorParsingException("Address/Port string incorrectly formed: " + addrport);
		currentCertificate.setDirectoryAddress(IPv4Address.createFromString(args[0]));
		currentCertificate.setDirectoryPort(fieldParser.parsePort(args[1]));
	}
	
	private void verifyCrossSignature(TorSignature crossSignature) {
		TorPublicKey identityKey = currentCertificate.getAuthorityIdentityKey();
		TorPublicKey signingKey = currentCertificate.getAuthoritySigningKey();
		if(!signingKey.verifySignature(crossSignature, identityKey.getFingerprint())) 
			throw new TorParsingException("Cross signature on certificate failed.");
	}

	private boolean verifyCurrentCertificate(TorSignature signature) {
		if(!fieldParser.verifySignedEntity(currentCertificate.getAuthorityIdentityKey(), signature)) {
			resultHandler.documentInvalid(currentCertificate, "Signature failed");
			fieldParser.logWarn("Signature failed for certificate with fingerprint: "+ currentCertificate.getAuthorityFingerprint());
			return false;
		}
		currentCertificate.setValidSignature();
		final boolean isValid = currentCertificate.isValidDocument();
		if(!isValid) {
			resultHandler.documentInvalid(currentCertificate, "Certificate data is invalid");
			fieldParser.logWarn("Certificate data is invalid for certificate with fingerprint: "+ currentCertificate.getAuthorityFingerprint());
		}
		return isValid;
	}
	
	private void processCertificateSignature() {
		fieldParser.endSignedEntity();
		if(verifyCurrentCertificate(fieldParser.parseSignature())) {
			currentCertificate.setRawDocumentData(fieldParser.getRawDocument());
			resultHandler.documentParsed(currentCertificate);
		}
		startNewCertificate();
	}
}
