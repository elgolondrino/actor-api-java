package im.actor.api.mtp._internal.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.api.util.StreamingUtils.*;

public class ProtoMessage extends ProtoObject {

    public long messageId;

    public byte[] payload;

    public ProtoMessage(long messageId, byte[] payload) {
        this.messageId = messageId;
        this.payload = payload;
    }

    public ProtoMessage(InputStream stream) throws IOException {
        super(stream);
    }

    @Override
    public void writeObject(OutputStream bs) throws IOException {
        writeLong(messageId, bs);
        writeProtoBytes(payload, bs);
    }

    @Override
    public ProtoObject readObject(InputStream bs) throws IOException {
        messageId = readLong(bs);
        payload = readProtoBytes(bs);
        return this;
    }

    @Override
    public int getLength() {
        return 8 + varintSize(payload.length) + payload.length;
    }

    @Override
    public String toString() {
        return "ProtoMessage [#" + messageId + "]";
    }
}
