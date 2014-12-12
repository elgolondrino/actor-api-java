package im.actor.torlib.directory.parsing.consensus;

import java.util.Arrays;
import java.util.List;

import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.errors.TorParsingException;
import im.actor.torlib.directory.parsing.DocumentFieldParser;
import im.actor.torlib.directory.parsing.NameIntegerParameter;

public class PreambleSectionParser extends ConsensusDocumentSectionParser {
    private final static int CURRENT_DOCUMENT_VERSION = 3;
    private boolean isFirstLine = true;

    PreambleSectionParser(DocumentFieldParser parser, ConsensusDocument document) {
        super(parser, document);
    }

    String getNextStateKeyword() {
        return "dir-source";
    }

    ConsensusDocumentParser.DocumentSection getSection() {
        return ConsensusDocumentParser.DocumentSection.PREAMBLE;
    }

    ConsensusDocumentParser.DocumentSection nextSection() {
        return ConsensusDocumentParser.DocumentSection.AUTHORITY;
    }

    @Override
    void parseLine(DocumentKeyword keyword) {
        if (isFirstLine) {
            parseFirstLine(keyword);
        } else {
            processKeyword(keyword);
        }
    }

    private void processKeyword(DocumentKeyword keyword) {
        switch (keyword) {
            case NETWORK_STATUS_VERSION:
                throw new TorParsingException("Network status version may only appear on the first line of status document");
            case VOTE_STATUS:
                final String voteStatus = fieldParser.parseString();
                if (!voteStatus.equals("consensus"))
                    throw new TorParsingException("Unexpected vote-status type: " + voteStatus);
                break;
            case CONSENSUS_METHOD:
                break;

            case VALID_AFTER:
                document.setValidAfter(fieldParser.parseTimestamp());
                break;

            case FRESH_UNTIL:
                document.setFreshUntil(fieldParser.parseTimestamp());
                break;

            case VALID_UNTIL:
                document.setValidUntil(fieldParser.parseTimestamp());
                break;

            case VOTING_DELAY:
                document.setVoteDelaySeconds(fieldParser.parseInteger());
                document.setDistDelaySeconds(fieldParser.parseInteger());
                break;

            case CLIENT_VERSIONS:
                break;
            case SERVER_VERSIONS:
                break;
            case KNOWN_FLAGS:
                while (fieldParser.argumentsRemaining() > 0)
                    document.addKnownFlag(fieldParser.parseString());
                break;

            case PARAMS:
                parseParams();
                break;

            default:
                break;
        }

    }

    private void parseFirstLine(DocumentKeyword keyword) {
        if (keyword != DocumentKeyword.NETWORK_STATUS_VERSION)
            throw new TorParsingException("network-status-version not found at beginning of consensus document as expected.");

        final int documentVersion = fieldParser.parseInteger();

        if (documentVersion != CURRENT_DOCUMENT_VERSION)
            throw new TorParsingException("Unexpected consensus document version number: " + documentVersion);

        if (fieldParser.argumentsRemaining() > 0) {
            parseConsensusFlavor();
        }
        isFirstLine = false;
    }

    private void parseConsensusFlavor() {
        final String flavor = fieldParser.parseString();
        if ("ns".equals(flavor)) {
            // document.setConsensusFlavor(ConsensusFlavor.NS);
        } else if ("microdesc".equals(flavor)) {
            // document.setConsensusFlavor(ConsensusFlavor.MICRODESC);
        } else {
            fieldParser.logWarn("Unknown consensus flavor: " + flavor);
        }
    }

    private List<String> parseVersions(String versions) {
        return Arrays.asList(versions.split(","));
    }

    private void parseParams() {
        final int remaining = fieldParser.argumentsRemaining();
        for (int i = 0; i < remaining; i++) {
            NameIntegerParameter p = fieldParser.parseParameter();
            document.addParameter(p.getName(), p.getValue());
        }
    }
}
