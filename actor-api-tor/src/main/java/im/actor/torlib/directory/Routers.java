package im.actor.torlib.directory;

import im.actor.torlib.Router;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.DescriptorDocument;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class Routers {

    private final static Logger LOG = Logger.getLogger(Routers.class.getName());

    private final Map<HexDigest, RouterImpl> routersByIdentity = new HashMap<HexDigest, RouterImpl>();
    private final Map<String, RouterImpl> routersByNickname = new HashMap<String, RouterImpl>();
    private final CopyOnWriteArrayList<RouterImpl> routers = new CopyOnWriteArrayList<RouterImpl>();

    private RouterDescriptors descriptors;

    public Routers(RouterDescriptors descriptors) {
        this.descriptors = descriptors;
    }

    public RouterDescriptors getDescriptors() {
        return descriptors;
    }

    public int getRoutersCount() {
        return routersByIdentity.size();
    }

    public int getDownloadedDescriptorsCount() {
        int res = 0;
        for (Router r : routersByIdentity.values()) {
            if (!r.isDescriptorDownloadable())
                res++;
        }
        return res;
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
        return routersByNickname.get(name);
    }

    public Router getRouterByIdentity(HexDigest identity) {
        synchronized (routersByIdentity) {
            return routersByIdentity.get(identity);
        }
    }

    public List<Router> getAllRouters() {
        synchronized (routersByIdentity) {
            return new ArrayList<Router>(routersByIdentity.values());
        }
    }

    private void clearAll() {
        routersByIdentity.clear();
        routersByNickname.clear();
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

    public synchronized List<Router> getRoutersWithDownloadableDescriptors() {
        final List<Router> routers = new ArrayList<Router>();
        for (RouterImpl router : routersByIdentity.values()) {
            if (router.isDescriptorDownloadable())
                routers.add(router);
        }

        Collections.shuffle(routers);

        return routers;
    }

    public synchronized void applyNewConsensus(ConsensusDocument consensus) {
        final Map<HexDigest, RouterImpl> oldRouterByIdentity = new HashMap<HexDigest, RouterImpl>(routersByIdentity);

        clearAll();

        for (RouterStatus status : consensus.getRouterStatusEntries()) {
            if (status.hasFlag("Running") && status.hasFlag("Valid")) {
                addRouter(updateOrCreateRouter(status, oldRouterByIdentity));
            }
            final DescriptorDocument d = descriptors.getDescriptorForRouterStatus(status);
            if (d != null) {
                d.setLastListed(consensus.getValidAfterTime().getTime());
            }
        }

        LOG.fine("Loaded " + routersByIdentity.size() + " routers from consensus document");
    }


    private RouterImpl updateOrCreateRouter(RouterStatus status, Map<HexDigest, RouterImpl> knownRouters) {
        final RouterImpl router = knownRouters.get(status.getIdentity());
        if (router == null)
            return RouterImpl.createFromRouterStatus(descriptors, status);
        router.updateStatus(status);
        return router;
    }


}
