package im.actor.torlib;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.documents.DescriptorDocument;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptorDocument(DescriptorDocument descriptorDocument);
}
