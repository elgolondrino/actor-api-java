package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserLastSeen extends Update {

    public static final int HEADER = 0x9;
    public static UpdateUserLastSeen fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserLastSeen.class, data);
    }

    private int uid;
    private long date;

    public UpdateUserLastSeen(int uid, long date) {
        this.uid = uid;
        this.date = date;
    }

    public UpdateUserLastSeen() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.date = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
