package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupAvatarChanged extends Update {

    public static final int HEADER = 0x27;
    public static UpdateGroupAvatarChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupAvatarChanged.class, data);
    }

    private int groupId;
    private long rid;
    private int uid;
    private Avatar avatar;
    private long date;

    public UpdateGroupAvatarChanged(int groupId, long rid, int uid, Avatar avatar, long date) {
        this.groupId = groupId;
        this.rid = rid;
        this.uid = uid;
        this.avatar = avatar;
        this.date = date;
    }

    public UpdateGroupAvatarChanged() {

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

    public Avatar getAvatar() {
        return this.avatar;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.rid = values.getLong(5);
        this.uid = values.getInt(2);
        this.avatar = values.optObj(3, Avatar.class);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeLong(5, this.rid);
        writer.writeInt(2, this.uid);
        if (this.avatar != null) {
            writer.writeObject(3, this.avatar);
        }
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
