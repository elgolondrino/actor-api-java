package im.actor.torlib.directory.routers.exitpolicy;

import im.actor.utils.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
