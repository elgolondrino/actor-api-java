package im.actor.torlib;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.Descriptor;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(Descriptor descriptor);
}
