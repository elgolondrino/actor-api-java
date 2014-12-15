package im.actor.torlib.documents.downloader;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.circuits.TorStream;
import im.actor.utils.HexDigest;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.KeyCertificateDocument;
import im.actor.torlib.errors.DirectoryRequestFailedException;
import im.actor.torlib.errors.StreamConnectFailedException;

/**
 * Synchronously downloads directory documents.
 */
public class DirectoryDocumentRequestor {
    private final TorStream stream;

    public DirectoryDocumentRequestor(TorStream stream) {
        this.stream = stream;
    }

    public DescriptorDocument downloadBridgeDescriptor() throws DirectoryRequestFailedException {
        return fetchSingleDocument(new BridgeDescriptorFetcher());
    }

    public ConsensusDocument downloadCurrentConsensus() throws DirectoryRequestFailedException {
        return fetchSingleDocument(new ConsensusFetcher());
    }

    public List<KeyCertificateDocument> downloadKeyCertificates(List<ConsensusDocument.RequiredCertificate> required) throws DirectoryRequestFailedException {
        return fetchDocuments(new CertificateFetcher(required));
    }

    public List<DescriptorDocument> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException {
        return fetchDocuments(new DescriptorFetcher(fingerprints));
    }

    private <T> T fetchSingleDocument(DocumentFetcher<T> fetcher) throws DirectoryRequestFailedException {
        final List<T> result = fetchDocuments(fetcher);
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

    private <T> List<T> fetchDocuments(DocumentFetcher<T> fetcher) throws DirectoryRequestFailedException {
        try {
            final TorHttpConnection http = new TorHttpConnection(stream);
            try {
                return fetcher.requestDocuments(http);
            } finally {
                http.close();
            }
        } catch (IOException e) {
            throw new DirectoryRequestFailedException("I/O exception processing directory request", e);
        }
    }
}