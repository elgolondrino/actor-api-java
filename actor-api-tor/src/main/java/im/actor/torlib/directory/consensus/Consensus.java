package im.actor.torlib.directory.consensus;

import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import im.actor.torlib.documents.ConsensusDocument;
import im.actor.torlib.documents.RouterStatusDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class Consensus extends BserObject {
    public static Consensus fromConsensusDocument(ConsensusDocument document) {
        Consensus consensus1 = new Consensus();
        consensus1.setValidUntil((int) (document.getValidUntilTime().getTime() / 1000));
        consensus1.setValidAfter((int) (document.getValidAfterTime().getTime() / 1000));
        consensus1.setFreshUntil((int) (document.getFreshUntilTime().getTime() / 1000));

        for (RouterStatusDocument d : document.getRouterStatusEntries()) {
            RouterStatus status = new RouterStatus();
            status.setNickname(d.getNickname());
            status.setIdentity(d.getIdentity());
            status.setAddress(d.getAddress());
            status.setRouterPort(d.getRouterPort());
            status.setDirectoryPort(d.getDirectoryPort());
            status.setMicrodescriptorDigest(d.getMicrodescriptorDigest());
            status.setBandwidthEstimate(d.getEstimatedBandwidth());
            status.setHasBandwidth(d.hasBandwidth());
            status.setPublicationTime((int) (d.getPublicationTime().getTime() / 1000));
            status.setVersion(d.getAppVersion());
            for (String f : d.getFlags()) {
                StatusFlag flag = StatusFlag.parse(f);
                if (flag != null) {
                    status.setFlag(flag);
                }
            }
            status.setV3Ident(d.getV3Ident());
            consensus1.getRouters().add(status);
        }
        for (String key : document.getBandwidthWeights().keySet()) {
            consensus1.addBandwidthWeight(key, document.getBandwidthWeights().get(key));
        }
        return consensus1;
    }

    protected int validAfter;
    protected int freshUntil;
    protected int validUntil;

    protected List<RouterStatus> routers = new ArrayList<RouterStatus>();

    protected HashMap<String, Integer> weights = new HashMap<String, Integer>();

    public Consensus() {

    }

    public boolean isLive() {
        return validUntil * 1000L >= System.currentTimeMillis();
    }

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

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, validAfter);
        writer.writeInt(2, freshUntil);
        writer.writeInt(3, validUntil);
        writer.writeRepeatedObj(4, routers);

        for (String s : weights.keySet()) {
            writer.writeString(5, s);
            writer.writeInt(6, weights.get(s));
        }
    }

    @Override
    public void parse(BserValues values) throws IOException {
        validAfter = values.getInt(1);
        freshUntil = values.getInt(2);
        validUntil = values.getInt(3);

        routers = values.getRepeatedObj(4, RouterStatus.class);

        List<String> keys = values.getRepeatedString(5);
        List<Integer> vals = values.getRepeatedInt(6);
        weights.clear();

        for (int i = 0; i < keys.size(); i++) {
            weights.put(keys.get(i), vals.get(i));
        }
    }
}
