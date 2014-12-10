package im.actor.torlib.directory.downloader;

import java.nio.ByteBuffer;

import im.actor.torlib.ConsensusDocument;
import im.actor.torlib.directory.parsing.DocumentParser;

public class ConsensusFetcher extends DocumentFetcher<ConsensusDocument> {

    private final static String CONSENSUS_PATH = "/tor/status-vote/current/consensus";

    public ConsensusFetcher() {
    }

    @Override
    String getRequestPath() {
        return CONSENSUS_PATH;
    }

    @Override
    DocumentParser<ConsensusDocument> createParser(ByteBuffer response) {
        return PARSER_FACTORY.createConsensusDocumentParser(response);
    }
}
