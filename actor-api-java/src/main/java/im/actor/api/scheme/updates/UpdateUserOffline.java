package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserOffline extends Update {

    public static final int HEADER = 0x8;
    public static UpdateUserOffline fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserOffline.class, data);
    }

    private int uid;

    public UpdateUserOffline(int uid) {
        this.uid = uid;
    }

    public UpdateUserOffline() {

    }

    public int getUid() {
        return this.uid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
