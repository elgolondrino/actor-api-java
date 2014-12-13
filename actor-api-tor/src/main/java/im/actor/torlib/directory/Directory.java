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

    private final DirectoryStorage store;
    private StateFile stateFile;

    private RouterDescriptors descriptors;
    private Routers routers;

    public Directory(TorConfig config) {
        this.store = new DirectoryStorage(config);
        // this.stateFile = new StateFile(store, this);
        this.descriptors = new RouterDescriptors(store);
        this.routers = new Routers(descriptors);
    }

    public DirectoryStorage getStore() {
        return store;
    }

    public void applyStateFile(StateFile stateFile) {
        this.stateFile = stateFile;
    }

    public Routers getRouters() {
        return routers;
    }

    public void loadFromStore() {
        LOG.info("Loading routers cache");
        routers.load();

        LOG.info("loading state file");
        stateFile.parseBuffer(store.loadCacheFile(DirectoryStorage.CacheFile.STATE));
    }

    public void close() {
        descriptors.close();
    }


    public GuardEntry createGuardEntryFor(Router router) {
        return stateFile.createGuardEntryFor(router);
    }

    public List<GuardEntry> getGuardEntries() {
        return stateFile.getGuardEntries();
    }

    public void removeGuardEntry(GuardEntry entry) {
        stateFile.removeGuardEntry(entry);
    }

    public void addGuardEntry(GuardEntry entry) {
        stateFile.addGuardEntry(entry);
    }
}