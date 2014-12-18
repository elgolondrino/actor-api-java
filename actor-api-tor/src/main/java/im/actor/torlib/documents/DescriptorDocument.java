package im.actor.torlib.documents;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import im.actor.torlib.crypto.TorPublicKey;
import im.actor.utils.HexDigest;

public class DescriptorDocument implements Document {
    public enum CacheLocation {NOT_CACHED, CACHED_CACHEFILE, CACHED_JOURNAL}

    private TorPublicKey onionKey;
    private byte[] ntorOnionKey;
    private Set<String> familyMembers = Collections.emptySet();
    private ExitPorts acceptPorts;
    private ExitPorts rejectPorts;
    private String rawDocumentData;
    private HexDigest descriptorDigest;
    private long lastListed;
    private CacheLocation cacheLocation = CacheLocation.NOT_CACHED;

    public void setOnionKey(TorPublicKey onionKey) {
        this.onionKey = onionKey;
    }

    public void setNtorOnionKey(byte[] ntorOnionKey) {
        this.ntorOnionKey = ntorOnionKey;
    }

    public void addFamilyMember(String familyMember) {
        if (familyMembers.isEmpty()) {
            familyMembers = new HashSet<String>();
        }
        familyMembers.add(familyMember);
    }

    public void addAcceptPorts(String portlist) {
        acceptPorts = ExitPorts.createAcceptExitPorts(portlist);
    }

    public void addRejectPorts(String portlist) {
        rejectPorts = ExitPorts.createRejectExitPorts(portlist);
    }

    public void setRawDocumentData(String rawData) {
        this.rawDocumentData = rawData;
    }

    public void setDescriptorDigest(HexDigest descriptorDigest) {
        this.descriptorDigest = descriptorDigest;
    }

    public void setLastListed(long ts) {
        this.lastListed = ts;
    }

    public boolean isValidDocument() {
        return (descriptorDigest != null) && (onionKey != null);
    }

    public String getRawDocumentData() {
        return rawDocumentData;
    }

    public TorPublicKey getOnionKey() {
        return onionKey;
    }

    public byte[] getNTorOnionKey() {
        return ntorOnionKey;
    }

    public Set<String> getFamilyMembers() {
        return familyMembers;
    }

    public boolean exitPolicyAccepts(int port) {
        if (acceptPorts == null) {
            return false;
        }
        if (rejectPorts != null && !rejectPorts.acceptsPort(port)) {
            return false;
        }
        return acceptPorts.acceptsPort(port);
    }

    public HexDigest getDescriptorDigest() {
        return descriptorDigest;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DescriptorDocument))
            return false;
        final DescriptorDocument other = (DescriptorDocument) o;
        if (other.getDescriptorDigest() == null || descriptorDigest == null)
            return false;

        return other.getDescriptorDigest().equals(descriptorDigest);
    }

    public int hashCode() {
        if (descriptorDigest == null)
            return 0;
        return descriptorDigest.hashCode();
    }

    public long getLastListed() {
        return lastListed;
    }

    public void setCacheLocation(CacheLocation location) {
        this.cacheLocation = location;
    }

    public CacheLocation getCacheLocation() {
        return cacheLocation;
    }

    public int getBodyLength() {
        return rawDocumentData.length();
    }

    public ByteBuffer getRawDocumentBytes() {
        if (getRawDocumentData() == null) {
            return ByteBuffer.allocate(0);
        } else {
            return ByteBuffer.wrap(getRawDocumentData().getBytes(Charset.forName("ISO-8859-1")));
        }
    }
}
