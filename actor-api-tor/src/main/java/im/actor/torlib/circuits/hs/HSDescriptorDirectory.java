package im.actor.torlib.circuits.hs;

import im.actor.torlib.directory.routers.Router;
import im.actor.utils.HexDigest;

public class HSDescriptorDirectory {
	
	private final HexDigest descriptorId;
	private final Router directory;
	
	HSDescriptorDirectory(HexDigest descriptorId, Router directory) {
		this.descriptorId = descriptorId;
		this.directory = directory;
	}
	
	Router getDirectory() {
		return directory;
	}
	
	HexDigest getDescriptorId() {
		return descriptorId;
	}
	
	public String toString() {
		return descriptorId + " : " + directory;
	}

}
