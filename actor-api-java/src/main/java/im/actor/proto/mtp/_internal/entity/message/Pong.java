package im.actor.proto.mtp._internal.entity.message;

import im.actor.proto.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.proto.util.StreamingUtils.*;

public class Pong extends ProtoStruct {

    public static final byte HEADER = (byte) 0x02;

    private long randomId;

    public Pong(InputStream stream) throws IOException {
        super(stream);
    }

    public Pong(long randomId) {
        this.randomId = randomId;
    }

    public long getRandomId() {
        return randomId;
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
        writeLong(randomId, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        randomId = readLong(bs);
    }

    @Override
    public String toString() {
        return "Pong[" + randomId + "]";
    }
}
