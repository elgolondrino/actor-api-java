package im.actor.proto.mtp._internal.entity.message;

import im.actor.proto.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.proto.util.StreamingUtils.*;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class RequestResend extends ProtoStruct {

    public static final byte HEADER = (byte) 0x09;

    private long messageId;

    public RequestResend(long messageId) {
        this.messageId = messageId;
    }

    public RequestResend(InputStream stream) throws IOException {
        super(stream);
    }

    public long getMessageId() {
        return messageId;
    }

    @Override
    public int getLength() {
        return 8;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeLong(messageId, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        messageId = readLong(bs);
    }
}
