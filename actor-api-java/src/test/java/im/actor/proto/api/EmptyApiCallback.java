package im.actor.proto.api;

import com.google.protobuf.Message;

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
    public void onSeqFatUpdate(int seq, byte[] state, Message update, List<ActorApiScheme.User> users) {

    }

    @Override
    public void onSeqUpdate(int seq, byte[] state, Message update) {

    }

    @Override
    public void onSeqTooLong() {

    }

    @Override
    public void onWeakUpdate(long date, Message update) {

    }
}