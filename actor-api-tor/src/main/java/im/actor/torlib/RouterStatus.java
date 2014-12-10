package im.actor.torlib;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.data.Timestamp;
import im.actor.torlib.data.exitpolicy.ExitPorts;

public interface RouterStatus {
	String getNickname();
	HexDigest getIdentity();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();
	Timestamp getPublicationTime();
	IPv4Address getAddress();
	int getRouterPort();
	boolean isDirectory();
	int getDirectoryPort();
	boolean hasFlag(String flag);
	String getVersion();
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();
	ExitPorts getExitPorts();
}
