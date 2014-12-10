package im.actor.torlib.directory.parsing.consensus;

import im.actor.torlib.crypto.TorSignature;
import im.actor.torlib.data.HexDigest;

public class DirectorySignature {
	
	private final HexDigest identityDigest;
	private final HexDigest signingKeyDigest;
	private final TorSignature signature;
	private final boolean useSha256;
	
	DirectorySignature(HexDigest identityDigest, HexDigest signingKeyDigest, TorSignature signature, boolean useSha256) {
		this.identityDigest = identityDigest;
		this.signingKeyDigest = signingKeyDigest;
		this.signature = signature;
		this.useSha256 = useSha256;
	}
	
	public HexDigest getIdentityDigest() {
		return identityDigest;
	}
	
	public HexDigest getSigningKeyDigest() {
		return signingKeyDigest;
	}
	
	public TorSignature getSignature() {
		return signature;
	}
	
	public boolean useSha256() {
		return useSha256;
	}
}
