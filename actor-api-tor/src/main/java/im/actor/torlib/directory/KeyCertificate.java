package im.actor.torlib.directory;

import im.actor.torlib.Document;
import im.actor.torlib.Tor;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.data.Timestamp;

import java.nio.ByteBuffer;

/**
 * This class represents a key certificate document as specified in 
 * dir-spec.txt (section 3.1).  These documents are published by
 * directory authorities and bind a long-term identity key to a
 * more temporary signing key. 
 */
public class KeyCertificate implements Document {
	private IPv4Address directoryAddress;
	private int directoryPort;
	private HexDigest fingerprint;
	private TorPublicKey identityKey;
	private Timestamp keyPublished;
	private Timestamp keyExpires;
	private TorPublicKey signingKey;
	private String rawDocumentData;

	private boolean hasValidSignature = false;

	public void setDirectoryPort(int port) { this.directoryPort = port; }
	public void setDirectoryAddress(IPv4Address address) { this.directoryAddress = address; }
	public void setAuthorityFingerprint(HexDigest fingerprint) { this.fingerprint = fingerprint;}
	public void setAuthorityIdentityKey(TorPublicKey key) { this.identityKey = key; }
	public void setAuthoritySigningKey(TorPublicKey key) { this.signingKey = key; }
	public void setKeyPublishedTime(Timestamp time) { this.keyPublished = time; }
	public void setKeyExpiryTime(Timestamp time) { this.keyExpires = time; }
	public void setValidSignature() { hasValidSignature = true;}
	public void setRawDocumentData(String rawData) { rawDocumentData = rawData; }

	public boolean isValidDocument() {
		return hasValidSignature && (fingerprint != null) && (identityKey != null) &&
				(keyPublished != null) && (keyExpires != null) && (signingKey != null);
	}

	public IPv4Address getDirectoryAddress() {
		return directoryAddress;
	}

	public int getDirectoryPort() {
		return directoryPort;
	}

	public HexDigest getAuthorityFingerprint() {
		return fingerprint;
	}

	public TorPublicKey getAuthorityIdentityKey() {
		return identityKey;
	}

	public TorPublicKey getAuthoritySigningKey() {
		return signingKey;
	}

	public Timestamp getKeyPublishedTime() {
		return keyPublished;
	}

	public Timestamp getKeyExpiryTime() {
		return keyExpires;
	}

	public boolean isExpired() {
		if(keyExpires != null) {
			return keyExpires.hasPassed();
		} else {
			return false;
		}
	}

	public String getRawDocumentData() {
		return rawDocumentData;
	}

	public ByteBuffer getRawDocumentBytes() {
		if(getRawDocumentData() == null) {
			return ByteBuffer.allocate(0);
		} else {
			return ByteBuffer.wrap(getRawDocumentData().getBytes(Tor.getDefaultCharset()));
		}
	}

	public String toString() {
		return "(Certificate: address="+ directoryAddress +":"+ directoryPort
				+" fingerprint="+ fingerprint +" published="+ keyPublished +" expires="+ keyExpires +")"+
				"\nident="+ identityKey +" sign="+ signingKey;
	}
}
