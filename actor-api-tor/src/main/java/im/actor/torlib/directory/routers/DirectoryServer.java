package im.actor.torlib.directory.routers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.actor.utils.HexDigest;
import im.actor.torlib.directory.RouterStatus;
import im.actor.torlib.documents.KeyCertificateDocument;

/**
 * Represents a directory authority server or a directory cache.
 */
public class DirectoryServer extends RouterImpl {
    private final List<KeyCertificateDocument> certificates = new ArrayList<KeyCertificateDocument>();

    private int port;
    private HexDigest v3Ident;

    public DirectoryServer(RouterStatus status) {
        super(null, status);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setV3Ident(HexDigest fingerprint) {
        this.v3Ident = fingerprint;
    }

    public boolean isValid() {
        return true;
    }

    public HexDigest getV3Identity() {
        return v3Ident;
    }

    public KeyCertificateDocument getCertificateByFingerprint(HexDigest fingerprint) {
        for (KeyCertificateDocument kc : getCertificates()) {
            if (kc.getAuthoritySigningKey().getFingerprint().equals(fingerprint)) {
                return kc;
            }
        }
        return null;
    }

    public List<KeyCertificateDocument> getCertificates() {
        synchronized (certificates) {
            purgeExpiredCertificates();
            purgeOldCertificates();
            return new ArrayList<KeyCertificateDocument>(certificates);
        }
    }

    private void purgeExpiredCertificates() {
        Iterator<KeyCertificateDocument> it = certificates.iterator();
        while (it.hasNext()) {
            KeyCertificateDocument elem = it.next();
            if (elem.isExpired()) {
                it.remove();
            }
        }
    }

    private void purgeOldCertificates() {
        if (certificates.size() < 2) {
            return;
        }
        final KeyCertificateDocument newest = getNewestCertificate();
        final Iterator<KeyCertificateDocument> it = certificates.iterator();
        while (it.hasNext()) {
            KeyCertificateDocument elem = it.next();
            if (elem != newest && isMoreThan48HoursOlder(newest, elem)) {
                it.remove();
            }
        }
    }

    private KeyCertificateDocument getNewestCertificate() {
        KeyCertificateDocument newest = null;
        for (KeyCertificateDocument kc : certificates) {
            if (newest == null || getPublishedMilliseconds(newest) > getPublishedMilliseconds(kc)) {
                newest = kc;
            }
        }
        return newest;
    }

    private boolean isMoreThan48HoursOlder(KeyCertificateDocument newer, KeyCertificateDocument older) {
        final long milliseconds = 48 * 60 * 60 * 1000;
        return (getPublishedMilliseconds(newer) - getPublishedMilliseconds(older)) > milliseconds;
    }

    private long getPublishedMilliseconds(KeyCertificateDocument certificate) {
        return certificate.getKeyPublishedTime().getDate().getTime();
    }

    public void addCertificate(KeyCertificateDocument certificate) {
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
