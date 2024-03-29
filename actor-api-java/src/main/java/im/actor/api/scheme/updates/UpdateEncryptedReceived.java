package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateEncryptedReceived extends Update {

    public static final int HEADER = 0x12;
    public static UpdateEncryptedReceived fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateEncryptedReceived.class, data);
    }

    private Peer peer;
    private long rid;
    private long receivedDate;

    public UpdateEncryptedReceived(Peer peer, long rid, long receivedDate) {
        this.peer = peer;
        this.rid = rid;
        this.receivedDate = receivedDate;
    }

    public UpdateEncryptedReceived() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public long getRid() {
        return this.rid;
    }

    public long getReceivedDate() {
        return this.receivedDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.rid = values.getLong(2);
        this.receivedDate = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(2, this.rid);
        writer.writeLong(3, this.receivedDate);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
