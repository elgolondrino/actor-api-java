package im.actor.torlib.directory.consensus;

import im.actor.torlib.directory.ConsensusDocument;
import im.actor.torlib.directory.ConsensusDocument.ConsensusFlavor;
import im.actor.torlib.RouterStatus;
import im.actor.torlib.TorParsingException;
import im.actor.torlib.crypto.TorMessageDigest;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.parsing.DocumentFieldParser;

public class RouterStatusSectionParser extends ConsensusDocumentSectionParser {

	private RouterStatus currentEntry = null;
	
	RouterStatusSectionParser(DocumentFieldParser parser, ConsensusDocument document) {
		super(parser, document);
	}
	
	@Override
	void parseLine(DocumentKeyword keyword) {
		if(!keyword.equals(DocumentKeyword.R))
			assertCurrentEntry();
		switch(keyword) {
		case R:
			parseFirstLine();
			break;
		case S:
			parseFlags();
			break;
		case V:
			parseVersion();
			break;
		case W:
			parseBandwidth();
			break;
		case P:
			parsePortList();
			break;
		case M:
			parseMicrodescriptorHash();
			break;
		default:
			break;
		}
	}

	private void assertCurrentEntry() {
		if(currentEntry == null) 
			throw new TorParsingException("Router status entry must begin with an 'r' line");
	}
	
	private void addCurrentEntry() {
		assertCurrentEntry();
		document.addRouterStatusEntry(currentEntry);
		currentEntry = null;
	}
	
	private void parseFirstLine() {
		if(currentEntry != null)
			throw new TorParsingException("Unterminated router status entry.");
		currentEntry = new RouterStatus();
		currentEntry.setNickname(fieldParser.parseNickname());
		currentEntry.setIdentity(parseBase64Digest());
		if(document.getFlavor() != ConsensusFlavor.MICRODESC) {
			currentEntry.setDigest(parseBase64Digest());
		}
		currentEntry.setPublicationTime(fieldParser.parseTimestamp());
		currentEntry.setAddress(fieldParser.parseAddress());
		currentEntry.setRouterPort(fieldParser.parsePort());
		currentEntry.setDirectoryPort(fieldParser.parsePort());
	}
	
	private HexDigest parseBase64Digest() {
		return HexDigest.createFromDigestBytes(fieldParser.parseBase64Data());
	}
	
	private void parseFlags() {
		while(fieldParser.argumentsRemaining() > 0)
			currentEntry.addFlag(fieldParser.parseString());
	}
	
	private void parseVersion() {
		currentEntry.setVersion(fieldParser.parseConcatenatedString());
	}
	
	private void parseBandwidth() {
		while(fieldParser.argumentsRemaining() > 0) {
			final String[] parts = fieldParser.parseString().split("=");
			if(parts.length == 2)
				parseBandwidthItem(parts[0], fieldParser.parseInteger(parts[1]));
		}
		if(document.getFlavor() == ConsensusFlavor.MICRODESC) {
			addCurrentEntry();
		}
	}
	
	private void parseBandwidthItem(String key, int value) {
		if(key.equals("Bandwidth")) 
			currentEntry.setEstimatedBandwidth(value);
		else if(key.equals("Measured"))
			currentEntry.setMeasuredBandwidth(value);
	}
	
	private void parsePortList() {
		if(document.getFlavor() == ConsensusFlavor.MICRODESC) {
			throw new TorParsingException("'p' line does not appear in consensus flavor 'microdesc'");
		}
		final String arg = fieldParser.parseString();
		if(arg.equals("accept")) {
			currentEntry.setAcceptedPorts(fieldParser.parseString());
		} else if(arg.equals("reject")) {
			currentEntry.setRejectedPorts(fieldParser.parseString());
		}
		addCurrentEntry();
	}
	
	private void parseMicrodescriptorHash() {
		if(document.getFlavor() != ConsensusFlavor.MICRODESC) {
			throw new TorParsingException("'m' line is invalid unless consensus flavor is microdesc");
		}
		final byte[] hashBytes = fieldParser.parseBase64Data();
		if(hashBytes.length != TorMessageDigest.TOR_DIGEST256_SIZE) {
			throw new TorParsingException("'m' line has incorrect digest size "+ hashBytes.length +" != "+ TorMessageDigest.TOR_DIGEST256_SIZE);
		}
		currentEntry.setMicrodescriptorDigest(HexDigest.createFromDigestBytes(hashBytes));
	}

	@Override
	String getNextStateKeyword() {
		return "directory-footer";
	}

	@Override
	ConsensusDocumentParser.DocumentSection getSection() {
		return ConsensusDocumentParser.DocumentSection.ROUTER_STATUS;
	}
	
	ConsensusDocumentParser.DocumentSection nextSection() {
		return ConsensusDocumentParser.DocumentSection.FOOTER;
	}

}