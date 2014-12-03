package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupTitleChanged extends Update {

    public static final int HEADER = 0x26;
    public static UpdateGroupTitleChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupTitleChanged.class, data);
    }

    private int groupId;
    private long rid;
    private int uid;
    private String title;
    private long date;

    public UpdateGroupTitleChanged(int groupId, long rid, int uid, String title, long date) {
        this.groupId = groupId;
        this.rid = rid;
        this.uid = uid;
        this.title = title;
        this.date = date;
    }

    public UpdateGroupTitleChanged() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public long getRid() {
        return this.rid;
    }

    public int getUid() {
        return this.uid;
    }

    public String getTitle() {
        return this.title;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.rid = values.getLong(5);
        this.uid = values.getInt(2);
        this.title = values.getString(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(5, this.rid);
        writer.writeInt(2, this.uid);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(3, this.title);
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
