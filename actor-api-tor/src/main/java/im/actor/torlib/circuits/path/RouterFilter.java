package im.actor.torlib.circuits.path;

import im.actor.torlib.directory.routers.Router;

public interface RouterFilter {
	boolean filter(Router router);
}
