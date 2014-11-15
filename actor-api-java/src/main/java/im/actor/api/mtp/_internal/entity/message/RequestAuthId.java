package im.actor.api.mtp._internal.entity.message;

import im.actor.api.mtp._internal.entity.ProtoStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestAuthId extends ProtoStruct {

    public static final byte HEADER = (byte) 0xF0;

    public RequestAuthId() {
    }

    public RequestAuthId(InputStream stream) throws IOException {
        super(stream);
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    protected byte getHeader() {
        return HEADER;
    }

    @Override
    protected void writeBody(OutputStream bs) throws IOException {

    }

    @Override
    protected void readBody(InputStream bs) throws IOException {

    }

}