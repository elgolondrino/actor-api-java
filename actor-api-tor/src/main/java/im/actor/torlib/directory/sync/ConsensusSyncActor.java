package im.actor.torlib.directory.sync;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.DirectoryCircuit;
import im.actor.torlib.directory.DirectoryDownloader;
import im.actor.torlib.directory.routers.DirectoryServer;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.directory.TrustedAuthorities;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParserFactory;
import im.actor.torlib.directory.parsing.DocumentParserFactoryImpl;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.KeyCertificateDocument;
import im.actor.torlib.documents.downloader.DirectoryDocumentRequestor;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.Threading;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.errors.DirectoryRequestFailedException;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class ConsensusSyncActor extends TypedActor<ConsensusSyncInt> implements ConsensusSyncInt {

    public static ConsensusSyncInt get(final NewDirectory directory, final CircuitManager circuitManager) {
        return TypedCreator.typed(ActorSystem.system().actorOf(Props.create(ConsensusSyncActor.class, new ActorCreator<ConsensusSyncActor>() {
            @Override
            public ConsensusSyncActor create() {
                return new ConsensusSyncActor(directory, circuitManager);
            }
        }), "directories/" + directory.getId() + "/sync/consensus"), ConsensusSyncInt.class);
    }

    private final static DocumentParserFactory PARSER_FACTORY = new DocumentParserFactoryImpl();

    private final static TorRandom RANDOM = new TorRandom();

    private final static Logger LOG = Logger.getLogger(ConsensusSyncActor.class.getName());


    private final ExecutorService executor = Threading.newPool("DirectoryDownloadTask worker");

    private NewDirectory directory;
    private DirectoryStorage storage;

    private ConsensusDocument currentConsensus;
    private ConsensusDocument currentPendingConsensus;
    private List<ConsensusDocument.RequiredCertificate> requiredCertificates
            = new ArrayList<ConsensusDocument.RequiredCertificate>();

    private Date consensusDownloadTime = new Date();

    private volatile boolean isDownloadingCertificates;
    private volatile boolean isDownloadingConsensus;

    private final CircuitManager circuitManager;

    public ConsensusSyncActor(NewDirectory directory, CircuitManager circuitManager) {
        super(ConsensusSyncInt.class);
        this.directory = directory;
        this.storage = directory.getStore();
        this.circuitManager = circuitManager;
    }


    private ConsensusDocument loadConsensus() {
        ByteBuffer byteBuffer = storage.loadCacheFile(DirectoryStorage.CacheFile.CONSENSUS_MICRODESC);
        final DocumentParser<ConsensusDocument> parser = PARSER_FACTORY.createConsensusDocumentParser(byteBuffer);
        final DocumentParsingResult<ConsensusDocument> result = parser.parse();
        if (result.isOkay() && result.getDocument().isValidDocument()) {
            return result.getDocument();
        } else {
            return null;
        }
    }

    private void loadCertificates() {
        ByteBuffer byteBuffer = storage.loadCacheFile(DirectoryStorage.CacheFile.CERTIFICATES);
        final DocumentParser<KeyCertificateDocument> parser = PARSER_FACTORY.createKeyCertificateParser(byteBuffer);
        final DocumentParsingResult<KeyCertificateDocument> result = parser.parse();
        if (result.isOkay()) {
            for (KeyCertificateDocument cert : result.getParsedDocuments()) {
                addCertificate(cert);
            }
        }
    }


    @Override
    public void startSync() {
        LOG.info("Loading consensus...");
        ConsensusDocument consensusDocument = loadConsensus();
        if (consensusDocument != null) {
            LOG.info("Consensus loaded.");
            applyCurrentConsensus(consensusDocument);
        } else {
            LOG.info("Consensus NOT loaded.");
            applyCurrentConsensus(null);
        }
        LOG.info("Loading certificates...");
        loadCertificates();
        LOG.info("Loading directory state...");
        // OBSOLETE
        directory.loadFromStore();

        self().sendOnce(new CheckConsensus());
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof CheckConsensus) {
            checkConsensus();
            checkCertificates();
            self().sendOnce(new CheckConsensus(), 5000);
        } else if (message instanceof ConsensusDownloaded) {
            onConsensusDownloaded(((ConsensusDownloaded) message).consensusDocument);
        } else if (message instanceof ConsensusDownloadFailed) {
            onConsensusDownloadFailed();
        } else if (message instanceof CertificatesDownloaded) {
            onCertificatesDownloaded(((CertificatesDownloaded) message).certificates);
        } else if (message instanceof CertificateDownloadFailed) {
            onCertificatesDownloadFailed();
        }
    }

    private void checkConsensus() {
        if (isDownloadingConsensus) {
            return;
        }

        if (requiredCertificates.size() > 0) {
            // Waiting for certificates
            return;
        }

        if (currentConsensus == null || !currentConsensus.isLive() ||
                consensusDownloadTime.before(new Date())) {
            if (currentConsensus == null) {
                LOG.info("Downloading consensus: no consensus available");
            } else if (!currentConsensus.isLive()) {
                LOG.info("Downloading consensus: consensus is outdated");
            } else {
                LOG.info("Downloading consensus: consensus is required to download");
            }

            isDownloadingConsensus = true;
            executor.execute(new DownloadConsensusTask());
        }
    }

    private void checkCertificates() {
        if (isDownloadingCertificates || requiredCertificates.isEmpty()) {
            return;
        }

        isDownloadingCertificates = true;
        executor.execute(
                new DownloadCertificatesTask(new ArrayList<ConsensusDocument.RequiredCertificate>(requiredCertificates)));
    }

    private void onConsensusDownloaded(ConsensusDocument consensus) {
        isDownloadingConsensus = false;

        if (currentConsensus != null && consensus.getValidAfterTime().isBefore(currentConsensus.getValidAfterTime())) {
            LOG.warning("New consensus document is older than current consensus document");
            return;
        }

        switch (consensus.verifySignatures()) {
            case STATUS_FAILED:
                LOG.warning("Unable to verify signatures on consensus document, discarding...");
                return;

            case STATUS_NEED_CERTS:
                LOG.info("Downloading required certificates");
                currentPendingConsensus = consensus;
                requiredCertificates.addAll(consensus.getRequiredCertificates());
                self().sendOnce(new CheckConsensus());
                return;

            case STATUS_VERIFIED:
                break;
        }

        applyCurrentConsensus(consensus);
        storage.writeDocument(DirectoryStorage.CacheFile.CONSENSUS_MICRODESC, consensus);
    }

    private void onConsensusDownloadFailed() {
        isDownloadingConsensus = false;
    }

    private void onCertificatesDownloaded(List<KeyCertificateDocument> certificates) {
        isDownloadingCertificates = false;
        for (KeyCertificateDocument c : certificates) {
            addCertificate(c);
        }

        final List<KeyCertificateDocument> certs = new ArrayList<KeyCertificateDocument>();
        for (DirectoryServer ds : TrustedAuthorities.getInstance().getAuthorityServers()) {
            certs.addAll(ds.getCertificates());
        }
        storage.writeDocumentList(DirectoryStorage.CacheFile.CERTIFICATES, certs);
    }

    public void addCertificate(KeyCertificateDocument certificate) {
        final boolean wasRequired = removeRequiredCertificate(certificate);
        final DirectoryServer as = TrustedAuthorities.getInstance().getAuthorityServerByIdentity(certificate.getAuthorityFingerprint());
        if (as == null) {
            LOG.warning("Certificate read for unknown directory authority with identity: " + certificate.getAuthorityFingerprint());
            return;
        }
        as.addCertificate(certificate);

        if (currentPendingConsensus != null && wasRequired) {

            switch (currentPendingConsensus.verifySignatures()) {
                case STATUS_FAILED:
                    currentPendingConsensus = null;
                    break;
                case STATUS_VERIFIED:
                    applyCurrentConsensus(currentPendingConsensus);
                    storage.writeDocument(DirectoryStorage.CacheFile.CONSENSUS_MICRODESC, currentPendingConsensus);
                    currentPendingConsensus = null;
                    break;
                case STATUS_NEED_CERTS:
                    requiredCertificates.addAll(currentPendingConsensus.getRequiredCertificates());
                    break;
            }
        }
    }

    private boolean removeRequiredCertificate(KeyCertificateDocument certificate) {
        final Iterator<ConsensusDocument.RequiredCertificate> it = requiredCertificates.iterator();
        while (it.hasNext()) {
            ConsensusDocument.RequiredCertificate r = it.next();
            if (r.getSigningKey().equals(certificate.getAuthoritySigningKey().getFingerprint())) {
                requiredCertificates.remove(r);
                return true;
            }
        }
        return false;
    }

    private void onCertificatesDownloadFailed() {
        isDownloadingCertificates = false;
    }

    private void applyCurrentConsensus(ConsensusDocument consensus) {
        if (consensus != null) {
            currentConsensus = consensus;
            consensusDownloadTime = chooseDownloadTimeForConsensus(consensus);
            directory.applyConsensusDocument(consensus);
        } else {
            currentConsensus = null;
            consensusDownloadTime = null;
        }
    }

    @Override
    public void stopSync() {
        context().stopSelf();
    }

    private DirectoryCircuit openCircuit() throws DirectoryRequestFailedException {
        try {
            return circuitManager.openDirectoryCircuit();
        } catch (OpenFailedException e) {
            throw new DirectoryRequestFailedException("Failed to open directory circuit", e);
        }
    }

    private class DownloadConsensusTask implements Runnable {
        public void run() {
            try {
                final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openCircuit(), null);
                ConsensusDocument consensus = requestor.downloadCurrentConsensus();
                self().send(new ConsensusDownloaded(consensus));
            } catch (DirectoryRequestFailedException e) {
                LOG.warning("Failed to download current consensus document: " + e.getMessage());
                self().send(new ConsensusDownloadFailed());
            }
        }
    }

    private class DownloadCertificatesTask implements Runnable {

        private List<ConsensusDocument.RequiredCertificate> requiredCertificates;

        public DownloadCertificatesTask(List<ConsensusDocument.RequiredCertificate> requiredCertificates) {
            this.requiredCertificates = requiredCertificates;
        }

        public void run() {
            try {
                final DirectoryDocumentRequestor requestor = new DirectoryDocumentRequestor(openCircuit(), null);
                List<KeyCertificateDocument> certificates = requestor.downloadKeyCertificates(requiredCertificates);
                self().send(new CertificatesDownloaded(certificates));
            } catch (DirectoryRequestFailedException e) {
                LOG.warning("Failed to download key certificates: " + e.getMessage());
                self().send(new CertificateDownloadFailed());
            }
        }
    }

    private static class CheckConsensus {
    }

    private static class ConsensusDownloaded {
        private ConsensusDocument consensusDocument;

        public ConsensusDownloaded(ConsensusDocument consensusDocument) {
            this.consensusDocument = consensusDocument;
        }
    }

    private static class ConsensusDownloadFailed {

    }

    private static class CertificatesDownloaded {
        private List<KeyCertificateDocument> certificates;

        public CertificatesDownloaded(List<KeyCertificateDocument> certificates) {
            this.certificates = certificates;
        }
    }

    private static class CertificateDownloadFailed {

    }

    /**
     * dir-spec 5.1: Downloading network-status documents
     * <p>
     * To avoid swarming the caches whenever a consensus expires, the clients
     * download new consensuses at a randomly chosen time after the caches are
     * expected to have a fresh consensus, but before their consensus will
     * expire. (This time is chosen uniformly at random from the interval
     * between the time 3/4 into the first interval after the consensus is no
     * longer fresh, and 7/8 of the time remaining after that before the
     * consensus is invalid.)</p>
     * <p>
     * [For example, if a cache has a consensus that became valid at 1:00, and
     * is fresh until 2:00, and expires at 4:00, that cache will fetch a new
     * consensus at a random time between 2:45 and 3:50, since 3/4 of the
     * one-hour interval is 45 minutes, and 7/8 of the remaining 75 minutes is
     * 65 minutes.]</p>
     */
    private static Date chooseDownloadTimeForConsensus(ConsensusDocument consensus) {
        final long va = consensus.getValidAfterTime().getDate().getTime();
        final long fu = consensus.getFreshUntilTime().getDate().getTime();
        final long vu = consensus.getValidUntilTime().getDate().getTime();
        final long i1 = fu - va;
        final long start = fu + ((i1 * 3) / 4);
        final long i2 = ((vu - start) * 7) / 8;
        final long r = RANDOM.nextLong(i2);
        final long download = start + r;
        return new Date(download);
    }
}
