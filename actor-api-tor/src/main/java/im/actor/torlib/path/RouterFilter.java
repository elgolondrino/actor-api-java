package im.actor.torlib.path;

import im.actor.torlib.directory.routers.Router;

public interface RouterFilter {
	boolean filter(Router router);
}
