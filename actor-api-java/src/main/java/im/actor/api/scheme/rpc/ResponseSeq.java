package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseSeq extends Response {

    public static final int HEADER = 0x48;
    public static ResponseSeq fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseSeq.class, data);
    }

    private int seq;
    private byte[] state;

    public ResponseSeq(int seq, byte[] state) {
        this.seq = seq;
        this.state = state;
    }

    public ResponseSeq() {

    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
