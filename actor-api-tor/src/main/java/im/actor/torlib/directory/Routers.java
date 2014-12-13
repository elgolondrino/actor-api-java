package im.actor.torlib.directory;

import im.actor.torlib.Router;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class Routers {

    private final static Logger LOG = Logger.getLogger(Routers.class.getName());

    private final Object LOCK = new Object();

    private final Map<HexDigest, RouterImpl> routersByIdentity = new HashMap<HexDigest, RouterImpl>();
    private final Map<String, RouterImpl> routersByNickname = new HashMap<String, RouterImpl>();

    private RouterDescriptors descriptors;

    public Routers(RouterDescriptors descriptors) {
        this.descriptors = descriptors;
    }

    public RouterDescriptors getDescriptors() {
        return descriptors;
    }

    public int getRoutersCount() {
        synchronized (LOCK) {
            return routersByIdentity.size();
        }
    }

    public int getDownloadedDescriptorsCount() {
        synchronized (LOCK) {
            int res = 0;
            for (Router r : routersByIdentity.values()) {
                if (!r.isDescriptorDownloadable())
                    res++;
            }
            return res;
        }
    }


    public Router getRouterByName(String name) {
        if (name.equals("Unnamed")) {
            return null;
        }
        if (name.length() == 41 && name.charAt(0) == '$') {
            try {
                final HexDigest identity = HexDigest.createFromString(name.substring(1));
                return getRouterByIdentity(identity);
            } catch (Exception e) {
                return null;
            }
        }
        synchronized (LOCK) {
            return routersByNickname.get(name);
        }
    }

    public Router getRouterByIdentity(HexDigest identity) {
        synchronized (LOCK) {
            return routersByIdentity.get(identity);
        }
    }

    public List<Router> getAllRouters() {
        synchronized (LOCK) {
            return new ArrayList<Router>(routersByIdentity.values());
        }
    }

    public List<Router> getDirectoryRouters() {
        synchronized (LOCK) {
            ArrayList<Router> res = new ArrayList<Router>();
            for (Router r : routersByIdentity.values()) {
                if (r.isValid() && r.isHSDirectory() && r.getDirectoryPort() != 0) {
                    res.add(r);
                }
            }
            return res;
        }
    }

    public List<Router> getHsDirectoryRouters() {
        synchronized (LOCK) {
            ArrayList<Router> res = new ArrayList<Router>();
            for (Router r : routersByIdentity.values()) {
                if (r.isRunning() && r.isValid() && r.isHSDirectory()) {
                    res.add(r);
                }
            }
            return res;
        }
    }

    public List<Router> getUsableRouters(boolean needDescriptor) {
        synchronized (LOCK) {
            final List<Router> routers = new ArrayList<Router>();
            for (Router r : routersByIdentity.values()) {
                if (r.isRunning() && r.isValid() &&
                        !(needDescriptor && r.getCurrentDescriptor() == null)) {

                    routers.add(r);
                }
            }

            return routers;
        }
    }

    public List<Router> getUsableExitRouters() {
        synchronized (LOCK) {
            final List<Router> result = new ArrayList<Router>();

            for (Router r : routersByIdentity.values()) {
                if (r.isRunning() && r.isValid()
                        && r.getCurrentDescriptor() != null
                        && r.isExit() && !r.isBadExit()) {

                    result.add(r);
                }
            }

            return result;
        }
    }

    public List<Router> getRoutersWithDownloadableDescriptors() {
        synchronized (LOCK) {
            final List<Router> routers = new ArrayList<Router>();
            for (RouterImpl router : routersByIdentity.values()) {
                if (router.isDescriptorDownloadable())
                    routers.add(router);
            }

            Collections.shuffle(routers);

            return routers;
        }
    }

    public void applyNewConsensus(ConsensusDocument consensus) {
        synchronized (LOCK) {
            final Map<HexDigest, RouterImpl> oldRouterByIdentity = new HashMap<HexDigest, RouterImpl>(routersByIdentity);

            routersByIdentity.clear();
            routersByNickname.clear();

            for (RouterStatus status : consensus.getRouterStatusEntries()) {
                if (status.hasFlag("Running") && status.hasFlag("Valid")) {
                    RouterImpl router = oldRouterByIdentity.get(status.getIdentity());
                    if (router == null) {
                        router = RouterImpl.createFromRouterStatus(descriptors, status);
                    } else {
                        router.updateStatus(status);
                    }
                    addRouter(router);
                }
                final DescriptorDocument d = descriptors.getDescriptorForRouterStatus(status);
                if (d != null) {
                    d.setLastListed(consensus.getValidAfterTime().getTime());
                }
            }

            LOG.fine("Loaded " + routersByIdentity.size() + " routers from consensus document");
        }
    }

    private void addRouter(RouterImpl router) {
        routersByIdentity.put(router.getIdentityHash(), router);

        final String name = router.getNickname();
        if (name == null || name.equals("Unnamed"))
            return;
        if (routersByNickname.containsKey(router.getNickname())) {
            //LOG.warn("Duplicate router nickname: "+ router.getNickname());
            return;
        }

        routersByNickname.put(name, router);
    }
}
