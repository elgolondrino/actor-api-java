package im.actor.torlib;

import im.actor.torlib.data.HexDigest;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(RouterDescriptor descriptor);
}
