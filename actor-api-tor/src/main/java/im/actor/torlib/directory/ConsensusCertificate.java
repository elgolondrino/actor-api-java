package im.actor.torlib.directory;

import im.actor.torlib.crypto.TorPublicKey;
import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class ConsensusCertificate {

    private TorPublicKey identityKey;
    private HexDigest fingerprint;

    private IPv4Address directoryAddress;
    private int directoryPort;

    private int keyPublished;
    private int keyExpires;
    private TorPublicKey signingKey;

    private boolean hasValidSignature = false;

    public TorPublicKey getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(TorPublicKey identityKey) {
        this.identityKey = identityKey;
    }

    public HexDigest getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(HexDigest fingerprint) {
        this.fingerprint = fingerprint;
    }

    public IPv4Address getDirectoryAddress() {
        return directoryAddress;
    }

    public void setDirectoryAddress(IPv4Address directoryAddress) {
        this.directoryAddress = directoryAddress;
    }

    public int getDirectoryPort() {
        return directoryPort;
    }

    public void setDirectoryPort(int directoryPort) {
        this.directoryPort = directoryPort;
    }

    public int getKeyPublished() {
        return keyPublished;
    }

    public void setKeyPublished(int keyPublished) {
        this.keyPublished = keyPublished;
    }

    public int getKeyExpires() {
        return keyExpires;
    }

    public void setKeyExpires(int keyExpires) {
        this.keyExpires = keyExpires;
    }

    public TorPublicKey getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(TorPublicKey signingKey) {
        this.signingKey = signingKey;
    }

    public boolean isHasValidSignature() {
        return hasValidSignature;
    }

    public void setHasValidSignature(boolean hasValidSignature) {
        this.hasValidSignature = hasValidSignature;
    }

    public boolean isExpired() {
        return keyExpires * 1000L <= System.currentTimeMillis();
    }

    public String toString() {
        return "(Certificate: address=" + directoryAddress + ":" + directoryPort
                + " fingerprint=" + fingerprint + " published=" + keyPublished + " expires=" + keyExpires + ")" +
                "\nident=" + identityKey + " sign=" + signingKey;
    }
}
