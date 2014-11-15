package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateMessageRead extends Update {

    public static final int HEADER = 0x13;
    public static UpdateMessageRead fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateMessageRead.class, data);
    }

    private Peer peer;
    private long date;
    private long readDate;

    public UpdateMessageRead(Peer peer, long date, long readDate) {
        this.peer = peer;
        this.date = date;
        this.readDate = readDate;
    }

    public UpdateMessageRead() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public long getDate() {
        return this.date;
    }

    public long getReadDate() {
        return this.readDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.date = values.getLong(2);
        this.readDate = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(2, this.date);
        writer.writeLong(3, this.readDate);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
