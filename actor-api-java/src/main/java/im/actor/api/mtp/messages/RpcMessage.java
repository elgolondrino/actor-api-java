package im.actor.api.mtp.messages;

/**
* Created by ex3ndr on 04.09.14.
*/
public class RpcMessage {
    private long messageId;
    private int payloadType;
    private byte[] payload;

    public RpcMessage(long messageId, int payloadType, byte[] payload) {
        this.messageId = messageId;
        this.payloadType = payloadType;
        this.payload = payload;
    }

    public long getMessageId() {
        return messageId;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public byte[] getPayload() {
        return payload;
    }
}
