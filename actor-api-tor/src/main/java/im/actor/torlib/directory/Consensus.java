package im.actor.torlib.directory;

import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.RouterStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class Consensus {
    public static Consensus fromConsensusDocument(ConsensusDocument document) {
        Consensus consensus1 = new Consensus();
        consensus1.setValidUntil((int) (document.getValidUntilTime().getTime() / 1000));
        consensus1.setValidAfter((int) (document.getValidAfterTime().getTime() / 1000));
        consensus1.setFreshUntil((int) (document.getFreshUntilTime().getTime() / 1000));
        consensus1.getRouters().addAll(document.getRouterStatusEntries());
        for (String key : document.getBandwidthWeights().keySet()) {
            consensus1.addBandwidthWeight(key, document.getBandwidthWeights().get(key));
        }
        return consensus1;
    }

    private int validAfter;
    private int freshUntil;
    private int validUntil;

    private Consensus() {

    }

    public boolean isLive() {
        return validUntil * 1000L >= System.currentTimeMillis();
    }

    private List<RouterStatus> routers = new ArrayList<RouterStatus>();

    private HashMap<String, Integer> weights = new HashMap<String, Integer>();

    public List<RouterStatus> getRouters() {
        return routers;
    }

    public int getValidAfter() {
        return validAfter;
    }

    public void setValidAfter(int validAfter) {
        this.validAfter = validAfter;
    }

    public int getFreshUntil() {
        return freshUntil;
    }

    public void setFreshUntil(int freshUntil) {
        this.freshUntil = freshUntil;
    }

    public int getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(int validUntil) {
        this.validUntil = validUntil;
    }

    public int getBandwidthWeight(String tag) {
        if (weights.containsKey(tag)) {
            return weights.get(tag);
        } else {
            return -1;
        }
    }

    public void addBandwidthWeight(String name, int value) {
        weights.put(name, value);
    }
}
