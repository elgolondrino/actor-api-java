package im.actor.torlib.directory;

import im.actor.torlib.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.ConsensusDocument;
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

    private final Directory obsoleteDirectory;

    private ConsensusDocument currentConsensus;

    public NewDirectory(Directory obsoleteDirectory, TorConfig config) {
        this.id = NEXT_ID.getAndIncrement();
        this.obsoleteDirectory = obsoleteDirectory;
        this.config = config;
    }

    public int getId() {
        return id;
    }

    public ConsensusDocument getCurrentConsensus() {
        return currentConsensus;
    }

    public Directory getObsoleteDirectory() {
        return obsoleteDirectory;
    }

    // Updating
    public void applyConsensusDocument(ConsensusDocument consensus) {
        obsoleteDirectory.getRouters().applyNewConsensus(consensus);
        this.currentConsensus = consensus;
    }

    public void applyRouterDescriptors(List<DescriptorDocument> descriptorDocuments) {
        obsoleteDirectory.getRouters().addRouterDescriptors(descriptorDocuments);
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
        return obsoleteDirectory.getRouters().haveMinimumRouterInfo();
    }

    public List<Router> getAllRouters() {
        return obsoleteDirectory.getRouters().getAllRouters();
    }

    public Router getRouterByName(String name) {
        return obsoleteDirectory.getRouters().getRouterByName(name);
    }

    public Router getRouterByIdentity(HexDigest identity) {
        return obsoleteDirectory.getRouters().getRouterByIdentity(identity);
    }

    public List<Router> getUsableRouters(boolean needDescriptor) {
        return obsoleteDirectory.getRouters().getUsableRouters(needDescriptor);
    }

    public List<Router> getUsableExitRouters() {
        return obsoleteDirectory.getRouters().getUsableExitRouters();
    }

    public List<Router> getDirectoryRouters() {
        return obsoleteDirectory.getRouters().getDirectoryRouters();
    }

    public List<Router> getHsDirectoryRouters() {
        return obsoleteDirectory.getRouters().getHsDirectoryRouters();
    }

    public List<Router> getRoutersWithDownloadableDescriptors() {
        return obsoleteDirectory.getRouters().getRoutersWithDownloadableDescriptors();
    }
}
