package im.actor.proto.api.messages;

import com.google.protobuf.Message;

/**
 * Created by ex3ndr on 04.09.14.
 */
public class WeakUpdate {
    private long date;
    private Message update;

    public WeakUpdate(long date, Message update) {
        this.date = date;
        this.update = update;
    }

    public long getDate() {
        return date;
    }

    public Message getUpdate() {
        return update;
    }
}
