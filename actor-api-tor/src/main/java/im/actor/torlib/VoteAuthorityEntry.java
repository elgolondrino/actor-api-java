package im.actor.torlib;

import java.util.List;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.directory.consensus.DirectorySignature;

public interface VoteAuthorityEntry {
	String getNickname();
	HexDigest getIdentity();
	String getHostname();
	IPv4Address getAddress();
	int getDirectoryPort();
	int getRouterPort();
	String getContact();
	HexDigest getVoteDigest();
	List<DirectorySignature> getSignatures();
}
