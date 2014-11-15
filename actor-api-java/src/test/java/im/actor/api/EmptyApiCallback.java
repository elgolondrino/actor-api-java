package im.actor.api;

import im.actor.api.ActorApiCallback;
import im.actor.api.parser.Update;
import im.actor.api.scheme.Group;
import im.actor.api.scheme.User;

import java.util.List;

/**
 * Created by ex3ndr on 10.11.14.
 */
public class EmptyApiCallback implements ActorApiCallback {
    @Override
    public void onAuthIdInvalidated() {

    }

    @Override
    public void onNewSessionCreated() {

    }

    @Override
    public void onSeqFatUpdate(int seq, byte[] state, Update update, List<User> users, List<Group> groups) {

    }

    @Override
    public void onSeqUpdate(int seq, byte[] state, Update update) {

    }

    @Override
    public void onSeqTooLong() {

    }

    @Override
    public void onWeakUpdate(long date, Update update) {

    }
}
