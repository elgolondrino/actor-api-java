package im.actor.torlib.circuits.actors.target;

import im.actor.utils.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
