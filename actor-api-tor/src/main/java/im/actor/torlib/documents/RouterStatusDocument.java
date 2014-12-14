package im.actor.torlib.documents;

import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;
import im.actor.utils.Timestamp;

import java.util.HashSet;
import java.util.Set;

public class RouterStatusDocument {
    private String nickname;
    private HexDigest identity;
    private HexDigest microdescriptorDigest;
    private Timestamp publicationTime;
    private IPv4Address address;
    private int routerPort;
    private int directoryPort;
    private Set<String> flags = new HashSet<String>();
    private String version;
    private int bandwidthEstimate;
    private boolean hasBandwidth;
    private HexDigest v3Ident;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIdentity(HexDigest identity) {
        this.identity = identity;
    }

    public void setMicrodescriptorDigest(HexDigest digest) {
        this.microdescriptorDigest = digest;
    }

    public void setPublicationTime(Timestamp timestamp) {
        this.publicationTime = timestamp;
    }

    public void setAddress(IPv4Address address) {
        this.address = address;
    }

    public void setRouterPort(int port) {
        this.routerPort = port;
    }

    public void setDirectoryPort(int port) {
        this.directoryPort = port;
    }

    public void addFlag(String flag) {
        this.flags.add(flag);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setEstimatedBandwidth(int bandwidth) {
        this.bandwidthEstimate = bandwidth;
        hasBandwidth = true;
    }

    public void setHiddenServiceAuthority() {
        addFlag("HSDir");
    }

    public void unsetHiddenServiceAuthority() {
        flags.remove("HSDir");
    }

    public void unsetV2Authority() {
        flags.remove("V2Dir");
    }

    public void setV3Ident(HexDigest v3Ident) {
        this.v3Ident = v3Ident;
    }

    public String getNickname() {
        return nickname;
    }

    public HexDigest getIdentity() {
        return identity;
    }

    public HexDigest getMicrodescriptorDigest() {
        return microdescriptorDigest;
    }

    public Timestamp getPublicationTime() {
        return publicationTime;
    }

    public IPv4Address getAddress() {
        return address;
    }

    public int getRouterPort() {
        return routerPort;
    }

    public boolean isDirectory() {
        return directoryPort != 0;
    }

    public int getDirectoryPort() {
        return directoryPort;
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public String getAppVersion() {
        return version;
    }

    public boolean hasBandwidth() {
        return hasBandwidth;
    }

    public int getEstimatedBandwidth() {
        return bandwidthEstimate;
    }

    public HexDigest getV3Ident() {
        return v3Ident;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public String toString() {
        return "Router: (" + nickname + " " + identity + " " + microdescriptorDigest + " " + address + " " + routerPort + " " + directoryPort
                + " " + version + ")";
    }


}
