package im.actor.torlib.directory.consensus;

import java.nio.ByteBuffer;

import im.actor.torlib.ConsensusDocument;
import im.actor.torlib.directory.downloader.DocumentFetcher;
import im.actor.torlib.directory.parsing.DocumentParser;

public class ConsensusFetcher extends DocumentFetcher<ConsensusDocument> {

    private final static String CONSENSUS_PATH = "/tor/status-vote/current/consensus-microdesc";

    public ConsensusFetcher() {
    }

    @Override
    public String getRequestPath() {
        return CONSENSUS_PATH;
    }

    @Override
    public DocumentParser<ConsensusDocument> createParser(ByteBuffer response) {
        return PARSER_FACTORY.createConsensusDocumentParser(response);
    }
}
