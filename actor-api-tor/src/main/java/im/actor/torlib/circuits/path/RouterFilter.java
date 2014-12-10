package im.actor.torlib.circuits.path;

import im.actor.torlib.Router;

public interface RouterFilter {
	boolean filter(Router router);
}
