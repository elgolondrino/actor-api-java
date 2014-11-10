package im.actor.proto.api.messages;

import com.google.protobuf.Message;
import im.actor.proto.api.ActorApiScheme;

import java.util.List;

/**
 * Created by ex3ndr on 30.09.14.
 */
public class SeqFatUpdate {
    private int seq;
    private byte[] state;
    private Message update;
    private List<ActorApiScheme.User> users;

    public SeqFatUpdate(int seq, byte[] state, Message update, List<ActorApiScheme.User> users) {
        this.seq = seq;
        this.state = state;
        this.update = update;
        this.users = users;
    }

    public int getSeq() {
        return seq;
    }

    public byte[] getState() {
        return state;
    }

    public Message getUpdate() {
        return update;
    }

    public List<ActorApiScheme.User> getUsers() {
        return users;
    }
}
