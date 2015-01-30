package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserStateChanged extends Update {

    public static final int HEADER = 0x64;
    public static UpdateUserStateChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserStateChanged.class, data);
    }

    private int uid;
    private UserState state;

    public UpdateUserStateChanged(int uid, UserState state) {
        this.uid = uid;
        this.state = state;
    }

    public UpdateUserStateChanged() {

    }

    public int getUid() {
        return this.uid;
    }

    public UserState getState() {
        return this.state;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.state = UserState.parse(values.getInt(2));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeInt(2, this.state.getValue());
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
