package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class HistoryMessage extends BserObject {

    private int senderUid;
    private long rid;
    private long date;
    private MessageContent message;

    public HistoryMessage(int senderUid, long rid, long date, MessageContent message) {
        this.senderUid = senderUid;
        this.rid = rid;
        this.date = date;
        this.message = message;
    }

    public HistoryMessage() {

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
        this.senderUid = values.getInt(1);
        this.rid = values.getLong(2);
        this.date = values.getLong(3);
        this.message = values.getObj(5, MessageContent.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.senderUid);
        writer.writeLong(2, this.rid);
        writer.writeLong(3, this.date);
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeObject(5, this.message);
    }

}
