package im.actor.torlib.directory;

import java.util.Collections;
import java.util.Set;

import im.actor.torlib.Router;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.errors.TorException;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.geoip.CountryCodeService;

public class RouterImpl implements Router {
    public static RouterImpl createFromRouterStatus(Directory directory, RouterStatus status) {
        return new RouterImpl(directory, status);
    }

    private final Directory directory;
    private final HexDigest identityHash;
    protected RouterStatus status;
    private DescriptorDocument descriptorDocument;

    private volatile String cachedCountryCode;

    protected RouterImpl(Directory directory, RouterStatus status) {
        this.directory = directory;
        this.identityHash = status.getIdentity();
        this.status = status;
        refreshDescriptor();
    }

    public void updateStatus(RouterStatus status) {
        if (!identityHash.equals(status.getIdentity()))
            throw new TorException("Identity hash does not match status update");
        this.status = status;
        this.cachedCountryCode = null;
        this.descriptorDocument = null;
        refreshDescriptor();
    }

    public boolean isDescriptorDownloadable() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return false;
        }

        final long now = System.currentTimeMillis();
        final long diff = now - status.getPublicationTime().getDate().getTime();
        return diff > (1000 * 60 * 10);
    }

    public String getVersion() {
        return status.getVersion();
    }

    public IPv4Address getAddress() {
        return status.getAddress();
    }

    public DescriptorDocument getCurrentDescriptor() {
        refreshDescriptor();
        return descriptorDocument;
    }

    private synchronized void refreshDescriptor() {
        if (descriptorDocument != null || directory == null) {
            return;
        }
        if (status.getMicrodescriptorDigest() != null) {
            descriptorDocument = directory.getDescriptorFromCache(status.getMicrodescriptorDigest());
        }
    }

    public HexDigest getMicrodescriptorDigest() {
        return status.getMicrodescriptorDigest();
    }

    public boolean hasFlag(String flag) {
        return status.hasFlag(flag);
    }

    public boolean isRunning() {
        return hasFlag("Running");
    }

    public boolean isValid() {
        return hasFlag("Valid");
    }

    public boolean isBadExit() {
        return hasFlag("BadExit");
    }

    public boolean isPossibleGuard() {
        return hasFlag("Guard");
    }

    public boolean isExit() {
        return hasFlag("Exit");
    }

    public boolean isFast() {
        return hasFlag("Fast");
    }

    public boolean isStable() {
        return hasFlag("Stable");
    }

    public boolean isHSDirectory() {
        return hasFlag("HSDir");
    }

    public int getDirectoryPort() {
        return status.getDirectoryPort();
    }

    public HexDigest getIdentityHash() {
        return identityHash;
    }

    public String getNickname() {
        return status.getNickname();
    }

    public int getOnionPort() {
        return status.getRouterPort();
    }

    public TorPublicKey getOnionKey() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return descriptorDocument.getOnionKey();
        } else {
            return null;
        }
    }

    public byte[] getNTorOnionKey() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return descriptorDocument.getNTorOnionKey();
        } else {
            return null;
        }
    }

    public boolean hasBandwidth() {
        return status.hasBandwidth();
    }

    public int getEstimatedBandwidth() {
        return status.getEstimatedBandwidth();
    }

    public Set<String> getFamilyMembers() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return descriptorDocument.getFamilyMembers();
        } else {
            return Collections.emptySet();
        }
    }

    public boolean exitPolicyAccepts(IPv4Address address, int port) {
        refreshDescriptor();
        if (descriptorDocument == null) {
            return false;
        } else if (address == null) {
            return descriptorDocument.exitPolicyAccepts(port);
        } else {
            return descriptorDocument.exitPolicyAccepts(address, port);
        }
    }

    public boolean exitPolicyAccepts(int port) {
        return exitPolicyAccepts(null, port);
    }

    public String toString() {
        return "Router[" + getNickname() + " (" + getAddress() + ":" + getOnionPort() + ")]";
    }

    public String getCountryCode() {
        String cc = cachedCountryCode;
        if (cc == null) {
            cc = CountryCodeService.getInstance().getCountryCodeForAddress(getAddress());
            cachedCountryCode = cc;
        }
        return cc;
    }
}
