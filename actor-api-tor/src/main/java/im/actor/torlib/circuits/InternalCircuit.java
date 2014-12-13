package im.actor.torlib.circuits;

import im.actor.torlib.directory.routers.Router;

public interface InternalCircuit extends Circuit {
	DirectoryCircuit cannibalizeToDirectory(Router target);
	Circuit cannibalizeToIntroductionPoint(Router target);
	HiddenServiceCircuit connectHiddenService(CircuitNode node);
}
