package im.actor.torlib.circuits.guards;

import java.util.Collections;
import java.util.Set;

import im.actor.torlib.BridgeRouter;
import im.actor.torlib.directory.Descriptor;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.geoip.CountryCodeService;

public class BridgeRouterImpl implements BridgeRouter {
	private final IPv4Address address;
	private final int port;
	
	private HexDigest identity;
	private Descriptor descriptor;
	
	private volatile String cachedCountryCode;
	
	BridgeRouterImpl(IPv4Address address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public IPv4Address getAddress() {
		return address;
	}

	public HexDigest getIdentity() {
		return identity;
	}
	
	public void setIdentity(HexDigest identity) {
		this.identity = identity;
	}

	public void setDescriptor(Descriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BridgeRouterImpl other = (BridgeRouterImpl) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}

	public String getNickname() {
		return toString();
	}

	public String getCountryCode() {
		String cc = cachedCountryCode;
		if(cc == null) {
			cc = CountryCodeService.getInstance().getCountryCodeForAddress(getAddress());
			cachedCountryCode = cc;
		}
		return cc;
	}

	public int getOnionPort() {
		return port;
	}

	public int getDirectoryPort() {
		return 0;
	}

	public TorPublicKey getIdentityKey() {
		return null;
	}

	public HexDigest getIdentityHash() {
		return identity;
	}

	public boolean isDescriptorDownloadable() {
		return false;
	}

	public String getVersion() {
		return "";
	}

	public Descriptor getCurrentDescriptor() {
		return descriptor;
	}

	public HexDigest getDescriptorDigest() {
		return null;
	}

	public HexDigest getMicrodescriptorDigest() {
		return null;
	}

	public TorPublicKey getOnionKey() {
		if(descriptor != null) {
			return descriptor.getOnionKey();
		} else {
			return null;
		}
	}

	public byte[] getNTorOnionKey() {
		if(descriptor != null) {
			return descriptor.getNTorOnionKey();
		} else {
			return null;
		}
	}

	public boolean hasBandwidth() {
		return false;
	}

	public int getEstimatedBandwidth() {
		return 0;
	}

	public int getMeasuredBandwidth() {
		return 0;
	}

	public Set<String> getFamilyMembers() {
		if(descriptor != null) {
			return descriptor.getFamilyMembers();
		} else {
			return Collections.emptySet();
		}
	}

	public int getAverageBandwidth() {
		return 0;
	}

	public int getBurstBandwidth() {
		return 0;
	}

	public int getObservedBandwidth() {
		return 0;
	}

	public boolean isHibernating() {
		return false;
	}

	public boolean isRunning() {
		return true;
	}

	public boolean isValid() {
		return true;
	}

	public boolean isBadExit() {
		return false;
	}

	public boolean isPossibleGuard() {
		return true;
	}

	public boolean isExit() {
		return false;
	}

	public boolean isFast() {
		return true;
	}

	public boolean isStable() {
		return true;
	}

	public boolean isHSDirectory() {
		return false;
	}

	public boolean exitPolicyAccepts(IPv4Address address, int port) {
		return false;
	}

	public boolean exitPolicyAccepts(int port) {
		return false;
	}

	public String toString() {
		return "[Bridge "+ address + ":"+ port + "]";
	}
}
