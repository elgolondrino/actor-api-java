package im.actor.proto.api;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface ActorApiStorage {
    public long getAuthKey();

    public void saveAuthKey(long key);
}
