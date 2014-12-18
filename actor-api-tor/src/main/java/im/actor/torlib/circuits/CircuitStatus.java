package im.actor.torlib.circuits;

public class CircuitStatus {

    enum CircuitState {
        OPEN("Open"),
        DESTROYED("Destroyed");
        String name;

        CircuitState(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private long timestampCreated;
    private long timestampDirty;

    private CircuitState state = CircuitState.OPEN;

    public CircuitStatus() {
        timestampCreated = System.currentTimeMillis();
        timestampDirty = 0;
    }

    public synchronized void updateDirtyTimestamp() {
        timestampDirty = System.currentTimeMillis();
    }

    public synchronized long getMillisecondsElapsedSinceCreated() {
        return millisecondsElapsedSince(timestampCreated);
    }

    public synchronized long getMillisecondsDirty() {
        return millisecondsElapsedSince(timestampDirty);
    }

    public synchronized boolean isDirty() {
        return timestampDirty != 0;
    }

    public synchronized boolean isConnected() {
        return state == CircuitState.OPEN;
    }

    public synchronized void destroy() {
        state = CircuitState.DESTROYED;
    }


    private static long millisecondsElapsedSince(long then) {
        if (then == 0) {
            return 0;
        }
        final long now = System.currentTimeMillis();
        return now - then;
    }
}
