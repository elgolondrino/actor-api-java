package im.actor.torlib.directory;

import im.actor.torlib.directory.consensus.Consensus;
import im.actor.utils.HexDigest;
import im.actor.torlib.directory.routers.*;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.documents.DescriptorDocument;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class NewDirectory {
    private final static AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final String dataPath;
    private final int id;
    private final DirectoryStorage store;

    private CopyOnWriteArrayList<DirectoryChangedListener> listeners = new CopyOnWriteArrayList<DirectoryChangedListener>();

    private Consensus currentConsensus;

    private Routers routers;

    public NewDirectory(String dataPath) {
        this.id = NEXT_ID.getAndIncrement();
        this.dataPath = dataPath;
        this.store = new DirectoryStorage(dataPath);
        this.routers = new Routers(store);
    }

    public void registerListener(DirectoryChangedListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void unregisterListener(DirectoryChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyDirectoryChanged() {
        for (DirectoryChangedListener l : listeners) {
            l.onDirectoryChanged();
        }
    }

    public String getDataPath() {
        return dataPath;
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
    }

    // Updating
    public void applyConsensusDocument(Consensus consensus) {
        routers.applyNewConsensus(consensus);
        this.currentConsensus = consensus;
        notifyDirectoryChanged();
    }

    public void applyRouterDescriptors(List<DescriptorDocument> descriptorDocuments) {
        routers.addRouterDescriptors(descriptorDocuments);
        notifyDirectoryChanged();
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
}
