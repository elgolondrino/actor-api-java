package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdatePhoneMoved extends Update {

    public static final int HEADER = 0x65;
    public static UpdatePhoneMoved fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdatePhoneMoved.class, data);
    }

    private int phoneId;
    private int uid;

    public UpdatePhoneMoved(int phoneId, int uid) {
        this.phoneId = phoneId;
        this.uid = uid;
    }

    public UpdatePhoneMoved() {

    }

    public int getPhoneId() {
        return this.phoneId;
    }

    public int getUid() {
        return this.uid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneId = values.getInt(1);
        this.uid = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.phoneId);
        writer.writeInt(2, this.uid);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
