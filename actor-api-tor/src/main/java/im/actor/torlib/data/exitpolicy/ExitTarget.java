package im.actor.torlib.data.exitpolicy;

import im.actor.torlib.data.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
