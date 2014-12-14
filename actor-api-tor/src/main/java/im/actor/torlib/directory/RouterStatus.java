package im.actor.torlib.directory;

import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class RouterStatus extends BserObject {
    protected String nickname;
    protected HexDigest identity;
    protected HexDigest v3Ident;
    protected HexDigest microdescriptorDigest;
    protected IPv4Address address;
    protected int routerPort;
    protected int directoryPort;
    protected int publicationTime;
    protected Set<StatusFlag> flags = new HashSet<StatusFlag>();
    protected String version;
    protected int bandwidthEstimate;
    protected boolean hasBandwidth;

    public RouterStatus() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public HexDigest getIdentity() {
        return identity;
    }

    public void setIdentity(HexDigest identity) {
        this.identity = identity;
    }

    public HexDigest getV3Ident() {
        return v3Ident;
    }

    public void setV3Ident(HexDigest v3Ident) {
        this.v3Ident = v3Ident;
    }

    public HexDigest getMicrodescriptorDigest() {
        return microdescriptorDigest;
    }

    public void setMicrodescriptorDigest(HexDigest microdescriptorDigest) {
        this.microdescriptorDigest = microdescriptorDigest;
    }

    public IPv4Address getAddress() {
        return address;
    }

    public void setAddress(IPv4Address address) {
        this.address = address;
    }

    public int getRouterPort() {
        return routerPort;
    }

    public void setRouterPort(int routerPort) {
        this.routerPort = routerPort;
    }

    public int getDirectoryPort() {
        return directoryPort;
    }

    public void setDirectoryPort(int directoryPort) {
        this.directoryPort = directoryPort;
    }

    public int getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(int publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBandwidthEstimate() {
        return bandwidthEstimate;
    }

    public void setBandwidthEstimate(int bandwidthEstimate) {
        this.bandwidthEstimate = bandwidthEstimate;
    }

    public boolean isHasBandwidth() {
        return hasBandwidth;
    }

    public void setHasBandwidth(boolean hasBandwidth) {
        this.hasBandwidth = hasBandwidth;
    }

    public void setFlag(StatusFlag flag) {
        this.flags.add(flag);
    }

    public void unsetFlag(StatusFlag flag) {
        this.flags.remove(flag);
    }

    public boolean hasFlag(StatusFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public void parse(BserValues values) throws IOException {
        nickname = values.getString(1);
        identity = HexDigest.createFromDigestBytes(values.getBytes(2));
        if (values.optBytes(3) != null) {
            v3Ident = HexDigest.createFromDigestBytes(values.getBytes(3));
        }
        if (values.optBytes(4) != null) {
            microdescriptorDigest = HexDigest.createFromDigestBytes(values.getBytes(4));
        }
        address = new IPv4Address(values.getInt(5));
        routerPort = values.getInt(6);
        directoryPort = values.getInt(7);
        publicationTime = values.getInt(8);
        version = values.optString(9);

        flags.clear();
        for (int i : values.getRepeatedInt(10)) {
            StatusFlag flag = StatusFlag.parse(i);
            if (flag != null) {
                flags.add(flag);
            }
        }

        hasBandwidth = values.getBool(11);
        bandwidthEstimate = values.optInt(12);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeString(1, nickname);
        writer.writeBytes(2, identity.getRawBytes());
        if (v3Ident != null) {
            writer.writeBytes(3, v3Ident.getRawBytes());
        }
        if (microdescriptorDigest != null) {
            writer.writeBytes(4, microdescriptorDigest.getRawBytes());
        }
        writer.writeInt(5, address.getAddressData());
        writer.writeInt(6, routerPort);
        writer.writeInt(7, directoryPort);
        writer.writeInt(8, publicationTime);
        if (version != null) {
            writer.writeString(9, version);
        }

        List<Integer> flags = new ArrayList<Integer>();
        for (StatusFlag f : this.flags) {
            flags.add(f.getVal());
        }
        writer.writeRepeatedInt(10, flags);

        writer.writeBool(11, hasBandwidth);
        writer.writeInt(12, bandwidthEstimate);
    }
}
