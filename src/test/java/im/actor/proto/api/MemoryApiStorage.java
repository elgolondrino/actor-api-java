package im.actor.proto.api;

/**
 * Created by ex3ndr on 10.11.14.
 */
public class MemoryApiStorage implements ActorApiStorage {

    private long key;

    @Override
    public long getAuthKey() {
        return key;
    }

    @Override
    public void saveAuthKey(long key) {
        this.key = key;
    }
}
