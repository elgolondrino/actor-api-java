package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserPhoneRemoved extends Update {

    public static final int HEADER = 0x58;
    public static UpdateUserPhoneRemoved fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserPhoneRemoved.class, data);
    }

    private int uid;
    private int phoneId;

    public UpdateUserPhoneRemoved(int uid, int phoneId) {
        this.uid = uid;
        this.phoneId = phoneId;
    }

    public UpdateUserPhoneRemoved() {

    }

    public int getUid() {
        return this.uid;
    }

    public int getPhoneId() {
        return this.phoneId;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.phoneId = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeInt(2, this.phoneId);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
