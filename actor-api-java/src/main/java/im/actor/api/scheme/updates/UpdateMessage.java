package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateMessage extends Update {

    public static final int HEADER = 0x37;
    public static UpdateMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateMessage.class, data);
    }

    private Peer peer;
    private int senderUid;
    private long date;
    private long rid;
    private MessageContent message;

    public UpdateMessage(Peer peer, int senderUid, long date, long rid, MessageContent message) {
        this.peer = peer;
        this.senderUid = senderUid;
        this.date = date;
        this.rid = rid;
        this.message = message;
    }

    public UpdateMessage() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getSenderUid() {
        return this.senderUid;
    }

    public long getDate() {
        return this.date;
    }

    public long getRid() {
        return this.rid;
    }

    public MessageContent getMessage() {
        return this.message;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.senderUid = values.getInt(2);
        this.date = values.getLong(3);
        this.rid = values.getLong(4);
        this.message = values.getObj(5, MessageContent.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeInt(2, this.senderUid);
        writer.writeLong(3, this.date);
        writer.writeLong(4, this.rid);
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeObject(5, this.message);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
