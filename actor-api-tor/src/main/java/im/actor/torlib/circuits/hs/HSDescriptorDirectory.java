package im.actor.torlib.circuits.hs;

import im.actor.torlib.directory.routers.Router;
import im.actor.utils.HexDigest;

public class HSDescriptorDirectory {
	
	private final HexDigest descriptorId;
	private final Router directory;

	public HSDescriptorDirectory(HexDigest descriptorId, Router directory) {
		this.descriptorId = descriptorId;
		this.directory = directory;
	}

	public Router getDirectory() {
		return directory;
	}

	public HexDigest getDescriptorId() {
		return descriptorId;
	}
	
	public String toString() {
		return descriptorId + " : " + directory;
	}

}
