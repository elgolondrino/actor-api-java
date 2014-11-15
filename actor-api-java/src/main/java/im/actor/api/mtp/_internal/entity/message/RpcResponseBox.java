package im.actor.api.mtp._internal.entity.message;

import im.actor.api.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.api.util.StreamingUtils.*;

public class RpcResponseBox extends ProtoStruct {

    public static final byte HEADER = (byte) 0x04;

    private long messageId;
    private byte[] payload;

    public RpcResponseBox(InputStream stream) throws IOException {
        super(stream);
    }

    public long getMessageId() {
        return messageId;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public int getLength() {
        return 1 + 8 + varintSize(payload.length) + payload.length;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeLong(messageId,bs);
        writeProtoBytes(payload, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        messageId = readLong(bs);
        payload = readProtoBytes(bs);
    }

    @Override
    public String toString() {
        return "ResponseBox [" + messageId + "]";
    }
}
