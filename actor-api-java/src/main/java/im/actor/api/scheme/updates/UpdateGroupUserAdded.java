package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupUserAdded extends Update {

    public static final int HEADER = 0x15;
    public static UpdateGroupUserAdded fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupUserAdded.class, data);
    }

    private int groupId;
    private int uid;
    private int inviterUid;
    private long date;

    public UpdateGroupUserAdded(int groupId, int uid, int inviterUid, long date) {
        this.groupId = groupId;
        this.uid = uid;
        this.inviterUid = inviterUid;
        this.date = date;
    }

    public UpdateGroupUserAdded() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getUid() {
        return this.uid;
    }

    public int getInviterUid() {
        return this.inviterUid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.uid = values.getInt(2);
        this.inviterUid = values.getInt(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeInt(2, this.uid);
        writer.writeInt(3, this.inviterUid);
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
