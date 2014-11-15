package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupUserLeave extends Update {

    public static final int HEADER = 0x17;
    public static UpdateGroupUserLeave fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupUserLeave.class, data);
    }

    private int groupId;
    private int uid;
    private long date;

    public UpdateGroupUserLeave(int groupId, int uid, long date) {
        this.groupId = groupId;
        this.uid = uid;
        this.date = date;
    }

    public UpdateGroupUserLeave() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getUid() {
        return this.uid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.uid = values.getInt(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeInt(2, this.uid);
        writer.writeLong(3, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
