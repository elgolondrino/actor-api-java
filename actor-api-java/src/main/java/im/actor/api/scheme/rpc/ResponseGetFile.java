package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetFile extends Response {

    public static final int HEADER = 0x11;
    public static ResponseGetFile fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetFile.class, data);
    }

    private byte[] payload;

    public ResponseGetFile(byte[] payload) {
        this.payload = payload;
    }

    public ResponseGetFile() {

    }

    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.payload = values.getBytes(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.payload == null) {
            throw new IOException();
        }
        writer.writeBytes(1, this.payload);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
