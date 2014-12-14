package im.actor.torlib.directory;

import im.actor.torlib.TorConfig;
import im.actor.utils.HexDigest;
import im.actor.torlib.directory.routers.*;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.documents.DescriptorDocument;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class NewDirectory {
    private final static AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final int id;
    private final TorConfig config;
    private final DirectoryStorage store;

    private Consensus currentConsensus;

    private Routers routers;

    private Guards guards;

    public NewDirectory(TorConfig config) {
        this.id = NEXT_ID.getAndIncrement();
        this.config = config;
        this.store = new DirectoryStorage(config);
        this.routers = new Routers(store);
        this.guards = new Guards(this);
    }

    public TorConfig getConfig() {
        return config;
    }

    public int getId() {
        return id;
    }

    public DirectoryStorage getStore() {
        return store;
    }

    public Consensus getCurrentConsensus() {
        return currentConsensus;
    }

    public void loadFromStore() {
        routers.load();
        guards.load();
    }

    // Updating
    public void applyConsensusDocument(Consensus consensus) {
        routers.applyNewConsensus(consensus);
        this.currentConsensus = consensus;
    }

    public void applyRouterDescriptors(List<DescriptorDocument> descriptorDocuments) {
        routers.addRouterDescriptors(descriptorDocuments);
    }


    public void close() {
        routers.close();
    }

    // Picking paths

//    public ArrayList<Router> pickInternalPath(Router endNode) {
//        return new ArrayList<Router>();
//    }
//
//    public ArrayList<Router> pickExternalPath(Router endNode) {
//        return new ArrayList<Router>();
//    }

    public DirectoryServer pickAuthority() {
        return TrustedAuthorities.getInstance().pickAuthority();
    }

    // Routers in directory

    public boolean haveMinimumRouterInfo() {
        return routers.haveMinimumRouterInfo();
    }

    public List<Router> getAllRouters() {
        return routers.getAllRouters();
    }

    public Router getRouterByName(String name) {
        return routers.getRouterByName(name);
    }

    public Router getRouterByIdentity(HexDigest identity) {
        return routers.getRouterByIdentity(identity);
    }

    public List<Router> getUsableRouters(boolean needDescriptor) {
        return routers.getUsableRouters(needDescriptor);
    }

    public List<Router> getUsableExitRouters() {
        return routers.getUsableExitRouters();
    }

    public List<Router> getDirectoryRouters() {
        return routers.getDirectoryRouters();
    }

    public List<Router> getHsDirectoryRouters() {
        return routers.getHsDirectoryRouters();
    }

    public List<Router> getRoutersWithDownloadableDescriptors() {
        return routers.getRoutersWithDownloadableDescriptors();
    }

    // Guards

    public GuardEntry createGuardEntryFor(Router router) {
        return guards.createGuardEntryFor(router);
    }

    public List<GuardEntry> getGuardEntries() {
        return guards.getGuardEntries();
    }

    public void removeGuardEntry(GuardEntry entry) {
        guards.removeGuardEntry(entry);
    }

    public void addGuardEntry(GuardEntry entry) {
        guards.addGuardEntry(entry);
    }
}
