package im.actor.torlib;

public interface TorInitializationListener {
	void initializationProgress(String message, int percent);
	void initializationCompleted();
}
