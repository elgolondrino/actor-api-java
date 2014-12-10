package im.actor.torlib.directory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.actor.torlib.KeyCertificate;
import im.actor.torlib.RouterStatus;
import im.actor.torlib.data.HexDigest;

/**
 * Represents a directory authority server or a directory cache.
 */
public class DirectoryServer extends RouterImpl {
    private List<KeyCertificate> certificates = new ArrayList<KeyCertificate>();

    private boolean isHiddenServiceAuthority = false;
    private boolean isBridgeAuthority = false;
    private boolean isExtraInfoCache = false;
    private int port;
    private HexDigest v3Ident;

    public DirectoryServer(RouterStatus status) {
        super(null, status);
    }

    public void setHiddenServiceAuthority() {
        isHiddenServiceAuthority = true;
    }

    public void unsetHiddenServiceAuthority() {
        isHiddenServiceAuthority = false;
    }

    public void setBridgeAuthority() {
        isBridgeAuthority = true;
    }

    public void setExtraInfoCache() {
        isExtraInfoCache = true;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setV3Ident(HexDigest fingerprint) {
        this.v3Ident = fingerprint;
    }

    public boolean isTrustedAuthority() {
        return true;
    }

    public boolean isValid() {
        return true;
    }

    public boolean isV2Authority() {
        return hasFlag("Authority") && hasFlag("V2Dir");
    }

    public boolean isV3Authority() {
        return hasFlag("Authority") && v3Ident != null;
    }

    public boolean isHiddenServiceAuthority() {
        return isHiddenServiceAuthority;
    }

    public boolean isBridgeAuthority() {
        return isBridgeAuthority;
    }

    public boolean isExtraInfoCache() {
        return isExtraInfoCache;
    }

    public HexDigest getV3Identity() {
        return v3Ident;
    }

    public KeyCertificate getCertificateByFingerprint(HexDigest fingerprint) {
        for (KeyCertificate kc : getCertificates()) {
            if (kc.getAuthoritySigningKey().getFingerprint().equals(fingerprint)) {
                return kc;
            }
        }
        return null;
    }

    public List<KeyCertificate> getCertificates() {
        synchronized (certificates) {
            purgeExpiredCertificates();
            purgeOldCertificates();
            return new ArrayList<KeyCertificate>(certificates);
        }
    }

    private void purgeExpiredCertificates() {
        Iterator<KeyCertificate> it = certificates.iterator();
        while (it.hasNext()) {
            KeyCertificate elem = it.next();
            if (elem.isExpired()) {
                it.remove();
            }
        }
    }

    private void purgeOldCertificates() {
        if (certificates.size() < 2) {
            return;
        }
        final KeyCertificate newest = getNewestCertificate();
        final Iterator<KeyCertificate> it = certificates.iterator();
        while (it.hasNext()) {
            KeyCertificate elem = it.next();
            if (elem != newest && isMoreThan48HoursOlder(newest, elem)) {
                it.remove();
            }
        }
    }

    private KeyCertificate getNewestCertificate() {
        KeyCertificate newest = null;
        for (KeyCertificate kc : certificates) {
            if (newest == null || getPublishedMilliseconds(newest) > getPublishedMilliseconds(kc)) {
                newest = kc;
            }
        }
        return newest;
    }

    private boolean isMoreThan48HoursOlder(KeyCertificate newer, KeyCertificate older) {
        final long milliseconds = 48 * 60 * 60 * 1000;
        return (getPublishedMilliseconds(newer) - getPublishedMilliseconds(older)) > milliseconds;
    }

    private long getPublishedMilliseconds(KeyCertificate certificate) {
        return certificate.getKeyPublishedTime().getDate().getTime();
    }

    public void addCertificate(KeyCertificate certificate) {
        if (!certificate.getAuthorityFingerprint().equals(v3Ident)) {
            throw new IllegalArgumentException("This certificate does not appear to belong to this directory authority");
        }
        synchronized (certificates) {
            certificates.add(certificate);
        }
    }

    public String toString() {
        if (v3Ident != null)
            return "(Directory: " + getNickname() + " " + getAddress() + ":" + port + " fingerprint=" + getIdentityHash() + " v3ident=" +
                    v3Ident + ")";
        else
            return "(Directory: " + getNickname() + " " + getAddress() + ":" + port + " fingerprint=" + getIdentityHash() + ")";

    }
}
