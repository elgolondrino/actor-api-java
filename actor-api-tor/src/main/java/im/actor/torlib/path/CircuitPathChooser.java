package im.actor.torlib.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.TorConfig;
import im.actor.utils.IPv4Address;
import im.actor.torlib.circuits.actors.target.ExitTarget;
import im.actor.torlib.directory.NewDirectory;

public class CircuitPathChooser {

    public static CircuitPathChooser create(TorConfig config, NewDirectory newDirectory) {
        return new CircuitPathChooser(newDirectory, new CircuitNodeChooser(config, newDirectory));
    }

    private final NewDirectory directory;
    private final CircuitNodeChooser nodeChooser;

    public CircuitPathChooser(NewDirectory directory, CircuitNodeChooser nodeChooser) {
        this.directory = directory;
        this.nodeChooser = nodeChooser;
    }

    public List<Router> chooseDirectoryPath() throws InterruptedException {
        final Router dir = nodeChooser.chooseDirectory();
        return Arrays.asList(dir);
    }

    public List<Router> chooseInternalPath() throws InterruptedException, PathSelectionFailedException {
        final Set<Router> excluded = Collections.emptySet();
        final Router finalRouter = chooseMiddleNode(excluded);
        return choosePathWithFinal(finalRouter);
    }

    public List<Router> choosePathWithExit(Router exitRouter) throws InterruptedException, PathSelectionFailedException {
        return choosePathWithFinal(exitRouter);
    }

    public List<Router> choosePathWithFinal(Router finalRouter) throws InterruptedException, PathSelectionFailedException {
        final Set<Router> excluded = new HashSet<Router>();
        excludeChosenRouterAndRelated(finalRouter, excluded);

        final Router middleRouter = chooseMiddleNode(excluded);
        if (middleRouter == null) {
            throw new PathSelectionFailedException("Failed to select suitable middle node");
        }
        excludeChosenRouterAndRelated(middleRouter, excluded);

        final Router entryRouter = chooseEntryNode(excluded);
        if (entryRouter == null) {
            throw new PathSelectionFailedException("Failed to select suitable entry node");
        }
        return Arrays.asList(entryRouter, middleRouter, finalRouter);
    }

    public Router chooseEntryNode(final Set<Router> excludedRouters) throws InterruptedException {
        return nodeChooser.chooseRandomNode(CircuitNodeChooser.WeightRule.WEIGHT_FOR_GUARD, new RouterFilter() {
            public boolean filter(Router router) {
                return router.isPossibleGuard() && !excludedRouters.contains(router);
            }
        });
    }

    Router chooseMiddleNode(final Set<Router> excludedRouters) {
        return nodeChooser.chooseRandomNode(CircuitNodeChooser.WeightRule.WEIGHT_FOR_MID, new RouterFilter() {
            public boolean filter(Router router) {
                return router.isFast() && !excludedRouters.contains(router);
            }
        });
    }

    public Router chooseExitNodeForTargets(List<ExitTarget> targets) {
        final List<Router> routers = filterForExitTargets(directory.getUsableExitRouters(), targets);
        return nodeChooser.chooseExitNode(routers);
    }


    private void excludeChosenRouterAndRelated(Router router, Set<Router> excludedRouters) {
        excludedRouters.add(router);
        for (Router r : directory.getAllRouters()) {
            if (areInSameSlash16(router, r)) {
                excludedRouters.add(r);
            }
        }

        for (String s : router.getFamilyMembers()) {
            Router r = directory.getRouterByName(s);
            if (r != null) {
                // Is mutual?
                if (isFamilyMember(r.getFamilyMembers(), router)) {
                    excludedRouters.add(r);
                }
            }
        }
    }

    private boolean isFamilyMember(Collection<String> familyMemberNames, Router r) {
        for (String s : familyMemberNames) {
            Router member = directory.getRouterByName(s);
            if (member != null && member.equals(r)) {
                return true;
            }
        }
        return false;
    }

    // Are routers r1 and r2 in the same /16 network
    private boolean areInSameSlash16(Router r1, Router r2) {
        final IPv4Address a1 = r1.getAddress();
        final IPv4Address a2 = r2.getAddress();
        final int mask = 0xFFFF0000;
        return (a1.getAddressData() & mask) == (a2.getAddressData() & mask);
    }

    private List<Router> filterForExitTargets(List<Router> routers, List<ExitTarget> exitTargets) {
        int bestSupport = 0;
        if (exitTargets.isEmpty()) {
            return routers;
        }

        final int[] nSupport = new int[routers.size()];

        for (int i = 0; i < routers.size(); i++) {
            final Router r = routers.get(i);
            nSupport[i] = countTargetSupport(r, exitTargets);
            if (nSupport[i] > bestSupport) {
                bestSupport = nSupport[i];
            }
        }

        if (bestSupport == 0) {
            return routers;
        }

        final List<Router> results = new ArrayList<Router>();
        for (int i = 0; i < routers.size(); i++) {
            if (nSupport[i] == bestSupport) {
                results.add(routers.get(i));
            }
        }
        return results;
    }

    private int countTargetSupport(Router router, List<ExitTarget> targets) {
        int count = 0;
        for (ExitTarget t : targets) {
            if (routerSupportsTarget(router, t)) {
                count += 1;
            }
        }
        return count;
    }

    private boolean routerSupportsTarget(Router router, ExitTarget target) {
        if (target.isAddressTarget()) {
            return router.exitPolicyAccepts(target.getAddress(), target.getPort());
        } else {
            return router.exitPolicyAccepts(target.getPort());
        }
    }
}