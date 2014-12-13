package im.actor.torlib.directory.routers;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.data.Timestamp;
import im.actor.torlib.data.exitpolicy.ExitPorts;

import java.util.HashSet;
import java.util.Set;

public class RouterStatus {
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
    private int bandwidthMeasured;
    private boolean hasBandwidth;
    private ExitPorts exitPorts;
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

    public void setMeasuredBandwidth(int bandwidth) {
        this.bandwidthMeasured = bandwidth;
    }

    public void setAcceptedPorts(String portList) {
        this.exitPorts = ExitPorts.createAcceptExitPorts(portList);
    }

    public void setRejectedPorts(String portList) {
        this.exitPorts = ExitPorts.createRejectExitPorts(portList);
    }

    public void setV1Authority() {
    }

    public void setHiddenServiceAuthority() {
        addFlag("HSDir");
    }

    public void unsetHiddenServiceAuthority() {
        flags.remove("HSDir");
    }

    public void setBridgeAuthority() {
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

    public String getVersion() {
        return version;
    }

    public boolean hasBandwidth() {
        return hasBandwidth;
    }

    public int getEstimatedBandwidth() {
        return bandwidthEstimate;
    }

    public int getMeasuredBandwidth() {
        return bandwidthMeasured;
    }

    public ExitPorts getExitPorts() {
        return exitPorts;
    }

    public HexDigest getV3Ident() {
        return v3Ident;
    }

    public String toString() {
        return "Router: (" + nickname + " " + identity + " " + microdescriptorDigest + " " + address + " " + routerPort + " " + directoryPort
                + " " + version + " " + exitPorts + ")";
    }


}
