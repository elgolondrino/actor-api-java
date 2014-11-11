package im.actor.stress;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Scenario {

    private Type type;
    private long startNumber;
    private int accountCount;
    private int delay;

    public Scenario(Type type, long startNumber, int accountCount, int delay) {
        this.type = type;
        this.startNumber = startNumber;
        this.accountCount = accountCount;
        this.delay = delay;
    }

    public Type getType() {
        return type;
    }

    public long getStartNumber() {
        return startNumber;
    }

    public int getAccountCount() {
        return accountCount;
    }

    public int getDelay() {
        return delay;
    }

    public enum Type {
        AUTH,
        MESSAGES
    }
}
