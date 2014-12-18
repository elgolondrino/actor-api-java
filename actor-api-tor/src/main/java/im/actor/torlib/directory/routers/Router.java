package im.actor.torlib.directory.routers;

import java.util.Collections;
import java.util.Set;

import im.actor.torlib.directory.consensus.RouterStatus;
import im.actor.torlib.directory.consensus.StatusFlag;
import im.actor.torlib.documents.DescriptorDocument;
import im.actor.torlib.errors.TorException;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;

public class Router {
    public static Router createFromRouterStatus(RouterDescriptors directory, RouterStatus status) {
        return new Router(directory, status);
    }

    private final RouterDescriptors directory;
    private final HexDigest identityHash;
    protected RouterStatus status;
    private DescriptorDocument descriptorDocument;

    protected Router(RouterDescriptors directory, RouterStatus status) {
        this.directory = directory;
        this.identityHash = status.getIdentity();
        this.status = status;
        refreshDescriptor();
    }

    public void updateStatus(RouterStatus status) {
        if (!identityHash.equals(status.getIdentity()))
            throw new TorException("Identity hash does not match status update");
        this.status = status;
        this.descriptorDocument = null;
        refreshDescriptor();
    }

    public boolean isDescriptorDownloadable() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return false;
        }

        final long now = System.currentTimeMillis();
        final long diff = now - status.getPublicationTime() * 1000L;
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

    public boolean hasFlag(StatusFlag flag) {
        return status.hasFlag(flag);
    }

    public boolean isRunning() {
        return hasFlag(StatusFlag.RUNNING);
    }

    public boolean isValid() {
        return hasFlag(StatusFlag.VALID);
    }

    public boolean isBadExit() {
        return hasFlag(StatusFlag.BAD_EXIT);
    }

    public boolean isPossibleGuard() {
        return hasFlag(StatusFlag.GUARD);
    }

    public boolean isExit() {
        return hasFlag(StatusFlag.EXIT);
    }

    public boolean isFast() {
        return hasFlag(StatusFlag.FAST);
    }

    public boolean isStable() {
        return hasFlag(StatusFlag.STABLE);
    }

    public boolean isHSDirectory() {
        return hasFlag(StatusFlag.HS_DIR);
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
        return status.isHasBandwidth();
    }

    public int getEstimatedBandwidth() {
        return status.getBandwidthEstimate();
    }

    public Set<String> getFamilyMembers() {
        refreshDescriptor();
        if (descriptorDocument != null) {
            return descriptorDocument.getFamilyMembers();
        } else {
            return Collections.emptySet();
        }
    }

    public boolean exitPolicyAccepts(int port) {
        refreshDescriptor();
        if (descriptorDocument == null) {
            return false;
        } else {
            return descriptorDocument.exitPolicyAccepts(port);
        }
    }

    public String toString() {
        return "Router[" + getNickname() + " (" + getAddress() + ":" + getOnionPort() + ")]";
    }
}
