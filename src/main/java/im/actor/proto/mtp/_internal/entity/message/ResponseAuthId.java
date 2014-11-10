package im.actor.proto.mtp._internal.entity.message;

import im.actor.proto.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.proto.util.StreamingUtils.*;

public class ResponseAuthId extends ProtoStruct {

    public static final int HEADER = (byte) 0xF1;

    public long authId;

    public ResponseAuthId(InputStream stream) throws IOException {
        super(stream);
    }

    public long getAuthId() {
        return authId;
    }

    @Override
    public int getLength() {
        return 1 + 8;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeLong(authId, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        authId = readLong(bs);
    }

    @Override
    public String toString() {
        return "ResponseAuthId[" + authId + "]";
    }
}