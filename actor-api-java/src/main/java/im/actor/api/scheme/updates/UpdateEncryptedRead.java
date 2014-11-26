package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateEncryptedRead extends Update {

    public static final int HEADER = 0x34;
    public static UpdateEncryptedRead fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateEncryptedRead.class, data);
    }

    private Peer peer;
    private long rid;
    private long readDate;

    public UpdateEncryptedRead(Peer peer, long rid, long readDate) {
        this.peer = peer;
        this.rid = rid;
        this.readDate = readDate;
    }

    public UpdateEncryptedRead() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public long getRid() {
        return this.rid;
    }

    public long getReadDate() {
        return this.readDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.rid = values.getLong(2);
        this.readDate = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(2, this.rid);
        writer.writeLong(3, this.readDate);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
