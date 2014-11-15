package im.actor.api.mtp._internal.entity.message;

import im.actor.api.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.api.util.StreamingUtils.*;

public class UpdateBox extends ProtoStruct {

    public static final byte HEADER = (byte) 0x05;

    private byte[] payload;

    public UpdateBox(InputStream stream) throws IOException {
        super(stream);
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public int getLength() {
        return 1 + payload.length;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeProtoBytes(payload, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        payload = readProtoBytes(bs);
    }

    @Override
    public String toString() {
        return "UpdateBox";
    }
}
