package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupUserKick extends Update {

    public static final int HEADER = 0x18;
    public static UpdateGroupUserKick fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupUserKick.class, data);
    }

    private int groupId;
    private int uid;
    private int kickerUid;
    private long date;

    public UpdateGroupUserKick(int groupId, int uid, int kickerUid, long date) {
        this.groupId = groupId;
        this.uid = uid;
        this.kickerUid = kickerUid;
        this.date = date;
    }

    public UpdateGroupUserKick() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getUid() {
        return this.uid;
    }

    public int getKickerUid() {
        return this.kickerUid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.uid = values.getInt(2);
        this.kickerUid = values.getInt(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeInt(2, this.uid);
        writer.writeInt(3, this.kickerUid);
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
