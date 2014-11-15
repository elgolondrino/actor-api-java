package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserNameChanged extends Update {

    public static final int HEADER = 0x20;
    public static UpdateUserNameChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserNameChanged.class, data);
    }

    private int uid;
    private String name;

    public UpdateUserNameChanged(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public UpdateUserNameChanged() {

    }

    public int getUid() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.name = values.getString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(2, this.name);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
