package im.actor.torlib.documents.downloader;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.circuits.TorStream;
import im.actor.torlib.state.TorInitializationTracker;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.KeyCertificateDocument;
import im.actor.torlib.errors.DirectoryRequestFailedException;
import im.actor.torlib.errors.StreamConnectFailedException;
import im.actor.torlib.utils.Tor;

/**
 * Synchronously downloads directory documents.
 */
public class DirectoryDocumentRequestor {
    private final static int OPEN_DIRECTORY_STREAM_TIMEOUT = 10 * 1000;

    private final DirectoryCircuit circuit;
    private final TorInitializationTracker initializationTracker;


    public DirectoryDocumentRequestor(DirectoryCircuit circuit) {
        this(circuit, null);
    }

    public DirectoryDocumentRequestor(DirectoryCircuit circuit, TorInitializationTracker initializationTracker) {
        this.circuit = circuit;
        this.initializationTracker = initializationTracker;
    }

    public DescriptorDocument downloadBridgeDescriptor() throws DirectoryRequestFailedException {
        return fetchSingleDocument(new BridgeDescriptorFetcher());
    }

    public ConsensusDocument downloadCurrentConsensus() throws DirectoryRequestFailedException {
        return fetchSingleDocument(new ConsensusFetcher(), CircuitManager.DIRECTORY_PURPOSE_CONSENSUS);
    }

    public List<KeyCertificateDocument> downloadKeyCertificates(List<ConsensusDocument.RequiredCertificate> required) throws DirectoryRequestFailedException {
        return fetchDocuments(new CertificateFetcher(required), CircuitManager.DIRECTORY_PURPOSE_CERTIFICATES);
    }

    public List<DescriptorDocument> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException {
        return fetchDocuments(new DescriptorFetcher(fingerprints), CircuitManager.DIRECTORY_PURPOSE_DESCRIPTORS);
    }

    private <T> T fetchSingleDocument(DocumentFetcher<T> fetcher) throws DirectoryRequestFailedException {
        return fetchSingleDocument(fetcher, 0);
    }

    private <T> T fetchSingleDocument(DocumentFetcher<T> fetcher, int purpose) throws DirectoryRequestFailedException {
        final List<T> result = fetchDocuments(fetcher, purpose);
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

    private <T> List<T> fetchDocuments(DocumentFetcher<T> fetcher, int purpose) throws DirectoryRequestFailedException {
        try {
            final TorHttpConnection http = createHttpConnection(purpose);
            try {
                return fetcher.requestDocuments(http);
            } finally {
                http.close();
            }
        } catch (TimeoutException e) {
            throw new DirectoryRequestFailedException("Directory request timed out");
        } catch (StreamConnectFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory stream", e);
        } catch (IOException e) {
            throw new DirectoryRequestFailedException("I/O exception processing directory request", e);
        } catch (InterruptedException e) {
            throw new DirectoryRequestFailedException("Directory request interrupted");
        }
    }

    private TorHttpConnection createHttpConnection(int purpose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        return new TorHttpConnection(openDirectoryStream(purpose));
    }

    private TorStream openDirectoryStream(int purpose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        final int requestEventCode = purposeToEventCode(purpose, false);
        final int loadingEventCode = purposeToEventCode(purpose, true);

        notifyInitialization(requestEventCode);

        final TorStream torStream = circuit.openDirectoryStream(OPEN_DIRECTORY_STREAM_TIMEOUT, true);
        notifyInitialization(loadingEventCode);
        return torStream;
    }

    private int purposeToEventCode(int purpose, boolean getLoadingEvent) {
        switch (purpose) {
            case CircuitManager.DIRECTORY_PURPOSE_CONSENSUS:
                return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_STATUS : Tor.BOOTSTRAP_STATUS_REQUESTING_STATUS;
            case CircuitManager.DIRECTORY_PURPOSE_CERTIFICATES:
                return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_KEYS : Tor.BOOTSTRAP_STATUS_REQUESTING_KEYS;
            case CircuitManager.DIRECTORY_PURPOSE_DESCRIPTORS:
                return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_DESCRIPTORS : Tor.BOOTSTRAP_STATUS_REQUESTING_DESCRIPTORS;
            default:
                return 0;
        }
    }

    private void notifyInitialization(int code) {
        if (code > 0 && initializationTracker != null) {
            initializationTracker.notifyEvent(code);
        }
    }
}