package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseEditGroupAvatar extends Response {

    public static final int HEADER = 0x73;
    public static ResponseEditGroupAvatar fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseEditGroupAvatar.class, data);
    }

    private Avatar avatar;
    private int seq;
    private byte[] state;
    private long date;

    public ResponseEditGroupAvatar(Avatar avatar, int seq, byte[] state, long date) {
        this.avatar = avatar;
        this.seq = seq;
        this.state = state;
        this.date = date;
    }

    public ResponseEditGroupAvatar() {

    }

    public Avatar getAvatar() {
        return this.avatar;
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
        this.avatar = values.getObj(1, Avatar.class);
        this.seq = values.getInt(2);
        this.state = values.getBytes(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.avatar == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.avatar);
        writer.writeInt(2, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.state);
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
