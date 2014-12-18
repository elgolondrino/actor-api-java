package im.actor.torlib.documents;

import im.actor.torlib.crypto.TorPublicKey;
import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;

public class IntroductionPoint {

	private HexDigest identity;
	private IPv4Address address;
	private int onionPort;
	private TorPublicKey onionKey;
	private TorPublicKey serviceKey;

	public IntroductionPoint(HexDigest identity) {
		this.identity = identity;
	}

	public void setAddress(IPv4Address address) {
		this.address = address;
	}

	public void setOnionPort(int onionPort) {
		this.onionPort = onionPort;
	}

	public void setOnionKey(TorPublicKey onionKey) {
		this.onionKey = onionKey;
	}

	public void setServiceKey(TorPublicKey serviceKey) {
		this.serviceKey = serviceKey;
	}

	public boolean isValidDocument() {
		return identity != null && address != null && onionPort != 0 && onionKey != null && serviceKey != null;
	}
	
	public HexDigest getIdentity() {
		return identity;
	}
	
	public IPv4Address getAddress() {
		return address;
	}
	
	public int getPort() {
		return onionPort;
	}
	
	public TorPublicKey getOnionKey() {
		return onionKey;
	}
	
	public TorPublicKey getServiceKey() {
		return serviceKey;
	}
}
