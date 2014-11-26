package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Dialog extends BserObject {

    private Peer peer;
    private int unreadCount;
    private long sortDate;
    private int senderUid;
    private long rid;
    private long date;
    private MessageContent message;

    public Dialog(Peer peer, int unreadCount, long sortDate, int senderUid, long rid, long date, MessageContent message) {
        this.peer = peer;
        this.unreadCount = unreadCount;
        this.sortDate = sortDate;
        this.senderUid = senderUid;
        this.rid = rid;
        this.date = date;
        this.message = message;
    }

    public Dialog() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getUnreadCount() {
        return this.unreadCount;
    }

    public long getSortDate() {
        return this.sortDate;
    }

    public int getSenderUid() {
        return this.senderUid;
    }

    public long getRid() {
        return this.rid;
    }

    public long getDate() {
        return this.date;
    }

    public MessageContent getMessage() {
        return this.message;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.unreadCount = values.getInt(3);
        this.sortDate = values.getLong(4);
        this.senderUid = values.getInt(5);
        this.rid = values.getLong(6);
        this.date = values.getLong(7);
        this.message = values.getObj(8, MessageContent.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeInt(3, this.unreadCount);
        writer.writeLong(4, this.sortDate);
        writer.writeInt(5, this.senderUid);
        writer.writeLong(6, this.rid);
        writer.writeLong(7, this.date);
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeObject(8, this.message);
    }

}
