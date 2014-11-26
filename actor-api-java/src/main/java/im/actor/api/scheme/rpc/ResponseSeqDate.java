package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseSeqDate extends Response {

    public static final int HEADER = 0x66;
    public static ResponseSeqDate fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseSeqDate.class, data);
    }

    private int seq;
    private byte[] state;
    private long date;

    public ResponseSeqDate(int seq, byte[] state, long date) {
        this.seq = seq;
        this.state = state;
        this.date = date;
    }

    public ResponseSeqDate() {

    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
        writer.writeLong(3, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
