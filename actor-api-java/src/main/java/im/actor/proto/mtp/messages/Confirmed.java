package im.actor.proto.mtp.messages;

/**
* Created by ex3ndr on 04.09.14.
*/
public class Confirmed {
    private long messageId;

    public Confirmed(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }
}
