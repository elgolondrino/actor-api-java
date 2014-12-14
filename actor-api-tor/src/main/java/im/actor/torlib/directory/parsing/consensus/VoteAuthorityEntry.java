package im.actor.torlib.directory.parsing.consensus;

import java.util.List;

import im.actor.utils.HexDigest;
import im.actor.utils.IPv4Address;

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
