package im.actor.torlib.circuits.path;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.crypto.TorRandom;

public class CircuitNodeChooser {
    private final static Logger logger = Logger.getLogger(CircuitNodeChooser.class.getName());

    public enum WeightRule {WEIGHT_FOR_DIR, WEIGHT_FOR_EXIT, WEIGHT_FOR_MID, WEIGHT_FOR_GUARD, NO_WEIGHTING}

    private final NewDirectory newDirectory;
    private final TorRandom random = new TorRandom();

    private final TorConfigNodeFilter configNodeFilter;


    public CircuitNodeChooser(TorConfig config, NewDirectory newDirectory) {
        this.newDirectory = newDirectory;
        this.configNodeFilter = new TorConfigNodeFilter(config);
    }

    /**
     * @param candidates
     * @return The chosen exit router or 'null' if no suitable router is available
     */
    public Router chooseExitNode(List<Router> candidates) {
        final List<Router> filteredCandidates = configNodeFilter.filterExitCandidates(candidates);
        return chooseByBandwidth(filteredCandidates, WeightRule.WEIGHT_FOR_EXIT);
    }

    public Router chooseDirectory() {
        final Router choice = chooseByBandwidth(newDirectory.getDirectoryRouters(), WeightRule.WEIGHT_FOR_DIR);
        if (choice == null) {
            return newDirectory.pickAuthority();
        } else {
            return choice;
        }
    }

    /**
     * @param rule
     * @param routerFilter
     * @return The chosen router or 'null' if no suitable router is available.
     */
    public Router chooseRandomNode(WeightRule rule, RouterFilter routerFilter) {
        final List<Router> candidates = getFilteredRouters(routerFilter, true);
        final Router choice = chooseByBandwidth(candidates, rule);
        if (choice == null) {
            // try again with more permissive flags
            return null;
        }
        return choice;
    }

    private List<Router> getFilteredRouters(RouterFilter rf, boolean needDescriptor) {
        final List<Router> routers = new ArrayList<Router>();
        for (Router r : newDirectory.getUsableRouters(needDescriptor)) {
            if (rf.filter(r)) {
                routers.add(r);
            }
        }
        return routers;
    }

    private Router chooseByBandwidth(List<Router> candidates, WeightRule rule) {
        final Router choice = chooseNodeByBandwidthWeights(candidates, rule);
        if (choice != null) {
            return choice;
        } else {
            return chooseNodeByBandwidth(candidates, rule);
        }
    }

    private Router chooseNodeByBandwidthWeights(List<Router> candidates, WeightRule rule) {
        final ConsensusDocument consensus = newDirectory.getObsoleteDirectory().getCurrentConsensusDocument();
        if (consensus == null) {
            return null;
        }
        final BandwidthWeightedRouters bwr = computeWeightedBandwidths(candidates, consensus, rule);
        return bwr.chooseRandomRouterByWeight();
    }


    private BandwidthWeightedRouters computeWeightedBandwidths(List<Router> candidates, ConsensusDocument consensus, WeightRule rule) {
        final CircuitNodeChooserWeightParameters wp = CircuitNodeChooserWeightParameters.create(consensus, rule);
        if (!wp.isValid()) {
            logger.warning("Got invalid bandwidth weights. Falling back to old selection method");
            return null;
        }
        final BandwidthWeightedRouters weightedRouters = new BandwidthWeightedRouters();
        for (Router r : candidates) {
            double wbw = wp.calculateWeightedBandwidth(r);
            weightedRouters.addRouter(r, wbw);
        }
        return weightedRouters;
    }

    private Router chooseNodeByBandwidth(List<Router> routers, WeightRule rule) {
        final BandwidthWeightedRouters bwr = new BandwidthWeightedRouters();
        for (Router r : routers) {
            long bw = getRouterBandwidthBytes(r);
            if (bw == -1) {
                bwr.addRouterUnknown(r);
            } else {
                bwr.addRouter(r, bw);
            }
        }
        bwr.fixUnknownValues();
        if (bwr.isTotalBandwidthZero()) {
            if (routers.size() == 0) {
                return null;
            }

            final int idx = random.nextInt(routers.size());
            return routers.get(idx);
        }

        computeFinalWeights(bwr, rule);
        return bwr.chooseRandomRouterByWeight();
    }


    private final static double EPSILON = 0.1;

    private void computeFinalWeights(BandwidthWeightedRouters bwr, WeightRule rule) {
        final double exitWeight = calculateWeight(rule == WeightRule.WEIGHT_FOR_EXIT,
                bwr.getTotalExitBandwidth(), bwr.getTotalBandwidth());
        final double guardWeight = calculateWeight(rule == WeightRule.WEIGHT_FOR_GUARD,
                bwr.getTotalGuardBandwidth(), bwr.getTotalBandwidth());

        bwr.adjustWeights(exitWeight, guardWeight);
    }

    private double calculateWeight(boolean matchesRule, double totalByType, double total) {
        if (matchesRule || totalByType < EPSILON) {
            return 1.0;
        }
        final double result = 1.0 - (total / (3.0 * totalByType));
        if (result <= 0.0) {
            return 0.0;
        } else {
            return result;
        }
    }

    private long getRouterBandwidthBytes(Router r) {
        if (!r.hasBandwidth()) {
            return -1;
        } else {
            return kbToBytes(r.getEstimatedBandwidth());
        }
    }

    private long kbToBytes(long bw) {
        return (bw > (Long.MAX_VALUE / 1000) ? Long.MAX_VALUE : bw * 1000);
    }
}
