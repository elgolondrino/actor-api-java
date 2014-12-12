package im.actor.torlib;

import java.util.Set;

import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.documents.DescriptorDocument;

public interface Router {

    String getNickname();

    String getCountryCode();

    IPv4Address getAddress();

    int getOnionPort();

    int getDirectoryPort();

    HexDigest getIdentityHash();

    boolean isDescriptorDownloadable();

    String getVersion();

    DescriptorDocument getCurrentDescriptor();

    HexDigest getMicrodescriptorDigest();

    TorPublicKey getOnionKey();

    byte[] getNTorOnionKey();

    boolean hasBandwidth();

    int getEstimatedBandwidth();

    Set<String> getFamilyMembers();

    boolean isRunning();

    boolean isValid();

    boolean isBadExit();

    boolean isPossibleGuard();

    boolean isExit();

    boolean isFast();

    boolean isStable();

    boolean isHSDirectory();

    boolean exitPolicyAccepts(IPv4Address address, int port);

    boolean exitPolicyAccepts(int port);
}
