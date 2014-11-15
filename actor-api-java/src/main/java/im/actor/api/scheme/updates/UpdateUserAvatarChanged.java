package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserAvatarChanged extends Update {

    public static final int HEADER = 0x10;
    public static UpdateUserAvatarChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserAvatarChanged.class, data);
    }

    private int uid;
    private Avatar avatar;

    public UpdateUserAvatarChanged(int uid, Avatar avatar) {
        this.uid = uid;
        this.avatar = avatar;
    }

    public UpdateUserAvatarChanged() {

    }

    public int getUid() {
        return this.uid;
    }

    public Avatar getAvatar() {
        return this.avatar;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.avatar = values.optObj(2, Avatar.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        if (this.avatar != null) {
            writer.writeObject(2, this.avatar);
        }
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
