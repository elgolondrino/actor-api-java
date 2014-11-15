package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateMessageReceived extends Update {

    public static final int HEADER = 0x36;
    public static UpdateMessageReceived fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateMessageReceived.class, data);
    }

    private Peer peer;
    private long date;
    private long receivedDate;

    public UpdateMessageReceived(Peer peer, long date, long receivedDate) {
        this.peer = peer;
        this.date = date;
        this.receivedDate = receivedDate;
    }

    public UpdateMessageReceived() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public long getDate() {
        return this.date;
    }

    public long getReceivedDate() {
        return this.receivedDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.date = values.getLong(2);
        this.receivedDate = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(2, this.date);
        writer.writeLong(3, this.receivedDate);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
