package im.actor.api.scheme.base;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateSeqUpdate extends RpcScope {

    public static final int HEADER = 0xd;
    public static UpdateSeqUpdate fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateSeqUpdate.class, data);
    }

    private int seq;
    private byte[] state;
    private int updateHeader;
    private byte[] update;

    public UpdateSeqUpdate(int seq, byte[] state, int updateHeader, byte[] update) {
        this.seq = seq;
        this.state = state;
        this.updateHeader = updateHeader;
        this.update = update;
    }

    public UpdateSeqUpdate() {

    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    public int getUpdateHeader() {
        return this.updateHeader;
    }

    public byte[] getUpdate() {
        return this.update;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
        this.updateHeader = values.getInt(3);
        this.update = values.getBytes(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
        writer.writeInt(3, this.updateHeader);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.update);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
