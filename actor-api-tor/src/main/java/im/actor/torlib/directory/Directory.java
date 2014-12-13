package im.actor.torlib.directory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import im.actor.torlib.*;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.ConsensusDocument.RequiredCertificate;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.cache.DescriptorCache;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParserFactory;
import im.actor.torlib.directory.parsing.DocumentParserFactoryImpl;
import im.actor.torlib.directory.parsing.DocumentParsingResult;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.directory.storage.StateFile;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.documents.KeyCertificateDocument;

/**
 * Main interface for accessing directory information and interacting
 * with directory authorities and caches.
 */
public class Directory {
    private final static Logger LOG = Logger.getLogger(Directory.class.getName());
    private final static DocumentParserFactory PARSER_FACTORY = new DocumentParserFactoryImpl();
    private final static AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final Object loadLock = new Object();
    private boolean isLoaded = false;

    private final int id;

    private final DirectoryStorage store;
    private final StateFile stateFile;

    private final Set<RequiredCertificate> requiredCertificates = new CopyOnWriteArraySet<RequiredCertificate>();

    private boolean haveMinimumRouterInfo;
    private boolean needRecalculateMinimumRouterInfo;

    private ConsensusDocument currentConsensus;
    private ConsensusDocument consensusWaitingForCertificates;

    private RouterDescriptors descriptors;
    private Routers routers;

    public Directory(TorConfig config) {
        this.id = NEXT_ID.getAndIncrement();
        this.store = new DirectoryStorage(config);
        this.stateFile = new StateFile(store, this);
        this.descriptors = new RouterDescriptors(store);
        this.routers = new Routers(descriptors);
    }

    public int getId() {
        return id;
    }

    public synchronized boolean haveMinimumRouterInfo() {
        if (needRecalculateMinimumRouterInfo) {
            checkMinimumRouterInfo();
        }
        return haveMinimumRouterInfo;
    }

    private synchronized void checkMinimumRouterInfo() {
        if (currentConsensus == null || !currentConsensus.isLive()) {
            needRecalculateMinimumRouterInfo = true;
            haveMinimumRouterInfo = false;
            return;
        }

        int routerCount = routers.getRoutersCount();
        int descriptorCount = routers.getDownloadedDescriptorsCount();
        needRecalculateMinimumRouterInfo = false;
        haveMinimumRouterInfo = (descriptorCount * 4 > routerCount);
    }

    public void loadFromStore() {
        LOG.info("Loading cached network information from disk");

        synchronized (loadLock) {
            if (isLoaded) {
                return;
            }
            last = System.currentTimeMillis();
            LOG.info("Loading certificates");
            loadCertificates(store.loadCacheFile(DirectoryStorage.CacheFile.CERTIFICATES));
            logElapsed();

            LOG.info("Loading consensus");
            loadConsensus(store.loadCacheFile(DirectoryStorage.CacheFile.CONSENSUS_MICRODESC));
            logElapsed();

            LOG.info("Loading microdescriptor cache");
            descriptors.load();
            needRecalculateMinimumRouterInfo = true;
            logElapsed();

            LOG.info("loading state file");
            stateFile.parseBuffer(store.loadCacheFile(DirectoryStorage.CacheFile.STATE));
            logElapsed();

            isLoaded = true;
            loadLock.notifyAll();
        }
    }

    public void close() {
        descriptors.close();
    }

    private long last = 0;

    private void logElapsed() {
        final long now = System.currentTimeMillis();
        final long elapsed = now - last;
        last = now;
        LOG.fine("Loaded in " + elapsed + " ms.");
    }

    private void loadCertificates(ByteBuffer buffer) {
        final DocumentParser<KeyCertificateDocument> parser = PARSER_FACTORY.createKeyCertificateParser(buffer);
        final DocumentParsingResult<KeyCertificateDocument> result = parser.parse();
        if (testResult(result, "certificates")) {
            for (KeyCertificateDocument cert : result.getParsedDocuments()) {
                addCertificate(cert);
            }
        }
    }

    private void loadConsensus(ByteBuffer buffer) {
        final DocumentParser<ConsensusDocument> parser = PARSER_FACTORY.createConsensusDocumentParser(buffer);
        final DocumentParsingResult<ConsensusDocument> result = parser.parse();
        if (testResult(result, "consensus")) {
            addConsensusDocument(result.getDocument(), true);
        }
    }

    private boolean testResult(DocumentParsingResult<?> result, String type) {
        if (result.isOkay()) {
            return true;
        } else if (result.isError()) {
            LOG.warning("Parsing error loading " + type + " : " + result.getMessage());
        } else if (result.isInvalid()) {
            LOG.warning("Problem loading " + type + " : " + result.getMessage());
        } else {
            LOG.warning("Unknown problem loading " + type);
        }
        return false;
    }

    public void waitUntilLoaded() {
        synchronized (loadLock) {
            while (!isLoaded) {
                try {
                    loadLock.wait();
                } catch (InterruptedException e) {
                    LOG.warning("Thread interrupted while waiting for directory to load from disk");
                }
            }
        }
    }

    public Set<ConsensusDocument.RequiredCertificate> getRequiredCertificates() {
        return new HashSet<ConsensusDocument.RequiredCertificate>(requiredCertificates);
    }

    public void addCertificate(KeyCertificateDocument certificate) {
        synchronized (TrustedAuthorities.getInstance()) {
            final boolean wasRequired = removeRequiredCertificate(certificate);
            final DirectoryServer as = TrustedAuthorities.getInstance().getAuthorityServerByIdentity(certificate.getAuthorityFingerprint());
            if (as == null) {
                LOG.warning("Certificate read for unknown directory authority with identity: " + certificate.getAuthorityFingerprint());
                return;
            }
            as.addCertificate(certificate);

            if (consensusWaitingForCertificates != null && wasRequired) {

                switch (consensusWaitingForCertificates.verifySignatures()) {
                    case STATUS_FAILED:
                        consensusWaitingForCertificates = null;
                        return;

                    case STATUS_VERIFIED:
                        addConsensusDocument(consensusWaitingForCertificates, false);
                        consensusWaitingForCertificates = null;
                        return;

                    case STATUS_NEED_CERTS:
                        requiredCertificates.addAll(consensusWaitingForCertificates.getRequiredCertificates());
                        return;
                }
            }
        }
    }

    private boolean removeRequiredCertificate(KeyCertificateDocument certificate) {
        final Iterator<RequiredCertificate> it = requiredCertificates.iterator();
        while (it.hasNext()) {
            RequiredCertificate r = it.next();
            if (r.getSigningKey().equals(certificate.getAuthoritySigningKey().getFingerprint())) {
                requiredCertificates.remove(r);
                return true;
            }
        }
        return false;
    }

    public void storeCertificates() {
        synchronized (TrustedAuthorities.getInstance()) {
            final List<KeyCertificateDocument> certs = new ArrayList<KeyCertificateDocument>();
            for (DirectoryServer ds : TrustedAuthorities.getInstance().getAuthorityServers()) {
                certs.addAll(ds.getCertificates());
            }
            store.writeDocumentList(DirectoryStorage.CacheFile.CERTIFICATES, certs);
        }
    }

    public synchronized void addConsensusDocument(ConsensusDocument consensus, boolean fromCache) {
        if (consensus.equals(currentConsensus))
            return;

        if (currentConsensus != null && consensus.getValidAfterTime().isBefore(currentConsensus.getValidAfterTime())) {
            LOG.warning("New consensus document is older than current consensus document");
            return;
        }

        synchronized (TrustedAuthorities.getInstance()) {
            switch (consensus.verifySignatures()) {
                case STATUS_FAILED:
                    LOG.warning("Unable to verify signatures on consensus document, discarding...");
                    return;

                case STATUS_NEED_CERTS:
                    consensusWaitingForCertificates = consensus;
                    requiredCertificates.addAll(consensus.getRequiredCertificates());
                    return;

                case STATUS_VERIFIED:
                    break;
            }
            requiredCertificates.addAll(consensus.getRequiredCertificates());

        }

        routers.applyNewConsensus(consensus);

        currentConsensus = consensus;

        if (!fromCache) {
            storeCurrentConsensus();
        }
    }

    private void storeCurrentConsensus() {
        if (currentConsensus != null) {
            store.writeDocument(DirectoryStorage.CacheFile.CONSENSUS_MICRODESC, currentConsensus);
        }
    }


    public synchronized void addRouterDescriptors(List<DescriptorDocument> descriptorDocuments) {
        descriptors.addRouterDescriptors(descriptorDocuments);
        needRecalculateMinimumRouterInfo = true;
    }


    public ConsensusDocument getCurrentConsensusDocument() {
        return currentConsensus;
    }

    public boolean hasPendingConsensus() {
        synchronized (TrustedAuthorities.getInstance()) {
            return consensusWaitingForCertificates != null;
        }
    }

    public Router getRouterByName(String name) {
        waitUntilLoaded();
        return routers.getRouterByName(name);
    }

    public List<Router> getAllRouters() {
        waitUntilLoaded();
        return routers.getAllRouters();
    }

    public synchronized List<Router> getRoutersWithDownloadableDescriptors() {
        waitUntilLoaded();
        return routers.getRoutersWithDownloadableDescriptors();
    }

    public Router getRouterByIdentity(HexDigest identity) {
        waitUntilLoaded();
        return routers.getRouterByIdentity(identity);
    }

    public GuardEntry createGuardEntryFor(Router router) {
        waitUntilLoaded();
        return stateFile.createGuardEntryFor(router);
    }

    public List<GuardEntry> getGuardEntries() {
        waitUntilLoaded();
        return stateFile.getGuardEntries();
    }

    public void removeGuardEntry(GuardEntry entry) {
        waitUntilLoaded();
        stateFile.removeGuardEntry(entry);
    }

    public void addGuardEntry(GuardEntry entry) {
        waitUntilLoaded();
        stateFile.addGuardEntry(entry);
    }
}