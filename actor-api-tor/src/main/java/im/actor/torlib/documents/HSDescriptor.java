package im.actor.torlib.documents;

import java.util.ArrayList;
import java.util.List;

import im.actor.torlib.circuits.hs.HiddenService;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.crypto.TorRandom;
import im.actor.utils.HexDigest;
import im.actor.utils.Timestamp;

public class HSDescriptor {
    private final static long MS_24_HOURS = (24 * 60 * 60 * 1000);
    private final HiddenService hiddenService;
    private HexDigest descriptorId;
    private Timestamp publicationTime;
    private HexDigest secretIdPart;
    private TorPublicKey permanentKey;
    private int[] protocolVersions;
    private List<IntroductionPoint> introductionPoints;

    public HSDescriptor(HiddenService hiddenService) {
        this.hiddenService = hiddenService;
        introductionPoints = new ArrayList<IntroductionPoint>();
    }

    public HiddenService getHiddenService() {
        return hiddenService;
    }

    public void setPublicationTime(Timestamp ts) {
        this.publicationTime = ts;
    }

    public void setSecretIdPart(HexDigest secretIdPart) {
        this.secretIdPart = secretIdPart;
    }

    public void setDescriptorId(HexDigest descriptorId) {
        this.descriptorId = descriptorId;
    }

    public void setPermanentKey(TorPublicKey permanentKey) {
        this.permanentKey = permanentKey;
    }

    public void setProtocolVersions(int[] protocolVersions) {
        this.protocolVersions = protocolVersions;
    }

    public void addIntroductionPoint(IntroductionPoint ip) {
        introductionPoints.add(ip);
    }

    public HexDigest getDescriptorId() {
        return descriptorId;
    }

    public int getVersion() {
        return 2;
    }

    public TorPublicKey getPermanentKey() {
        return permanentKey;
    }

    public HexDigest getSecretIdPart() {
        return secretIdPart;
    }

    public Timestamp getPublicationTime() {
        return publicationTime;
    }

    public int[] getProtocolVersions() {
        return protocolVersions;
    }

    public boolean isExpired() {
        final long now = System.currentTimeMillis();
        final long then = publicationTime.getTime();
        return (now - then) > MS_24_HOURS;
    }

    public List<IntroductionPoint> getIntroductionPoints() {
        return new ArrayList<IntroductionPoint>(introductionPoints);
    }

    public List<IntroductionPoint> getShuffledIntroductionPoints() {
        return shuffle(getIntroductionPoints());
    }

    private List<IntroductionPoint> shuffle(List<IntroductionPoint> list) {
        final TorRandom r = new TorRandom();
        final int sz = list.size();
        for (int i = 0; i < sz; i++) {
            swap(list, i, r.nextInt(sz));
        }
        return list;
    }

    private void swap(List<IntroductionPoint> list, int a, int b) {
        if (a == b) {
            return;
        }
        final IntroductionPoint tmp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, tmp);
    }
}
