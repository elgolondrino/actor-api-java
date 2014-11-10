package im.actor.proto.api.messages;

import com.google.protobuf.Message;

/**
 * Created by ex3ndr on 04.09.14.
 */
public class SeqUpdate {
    private int seq;
    private byte[] state;
    private Message update;

    public SeqUpdate(int seq, byte[] state, Message update) {
        this.seq = seq;
        this.state = state;
        this.update = update;
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
}
