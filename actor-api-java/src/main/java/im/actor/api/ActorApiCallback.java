package im.actor.api;

import im.actor.api.parser.Update;
import im.actor.api.scheme.Group;
import im.actor.api.scheme.User;

import java.util.List;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface ActorApiCallback {
    public void onAuthIdInvalidated();

    public void onNewSessionCreated();

    public void onSeqFatUpdate(int seq, byte[] state, Update update,
                               List<User> users, List<Group> groups);

    public void onSeqUpdate(int seq, byte[] state, Update update);

    public void onSeqTooLong();

    public void onWeakUpdate(long date, Update update);
}
