package im.actor.proto.mtp._internal.entity.message.rpc;

import im.actor.proto.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static im.actor.proto.util.StreamingUtils.*;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class Update extends ProtoStruct {
    public int updateType;
    public byte[] body;

    public Update(InputStream stream) throws IOException {
        super(stream);
    }

    public Update(int updateType, byte[] body) {
        this.updateType = updateType;
        this.body = body;
    }

    @Override
    public byte getHeader() {
        return 0;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {
        writeInt(updateType, bs);
        writeProtoBytes(body, bs);
    }

    @Override
    protected void readBody(InputStream bs) throws IOException {
        updateType = readInt(bs);
        body = readProtoBytes(bs);
    }

    @Override
    public int getLength() {
        return 4 + body.length;
    }

    @Override
    public String toString() {
        return "Update[" + updateType + "]";
    }
}
