package im.actor.proto.api;

import com.google.protobuf.Message;

import java.util.List;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface ActorApiCallback {
    public void onAuthIdInvalidated();

    public void onNewSessionCreated();

    public void onSeqFatUpdate(int seq, byte[] state, Message update,
                               List<ActorApiScheme.User> users);

    public void onSeqUpdate(int seq, byte[] state, Message update);

    public void onSeqTooLong();

    public void onWeakUpdate(long date, Message update);
}
