package im.actor.proto.mtp._internal.entity.message;

import im.actor.proto.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.proto.util.StreamingUtils.*;

public class NewSession extends ProtoStruct {

    public static final byte HEADER = (byte) 0x0C;

    public long sessionId;

    public long messageId;

    public NewSession(InputStream stream) throws IOException {
        super(stream);
    }

    public NewSession(long sessionId, long messageId) {
        this.sessionId = sessionId;
        this.messageId = messageId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getMessageId() {
        return messageId;
    }

    @Override
    public int getLength() {
        return 1 + 8 + 8;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeLong(sessionId, bs);
        writeLong(messageId, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        sessionId = readLong(bs);
        messageId = readLong(bs);
    }

    @Override
    public String toString() {
        return "NewSession [" + sessionId + "]";
    }
}
