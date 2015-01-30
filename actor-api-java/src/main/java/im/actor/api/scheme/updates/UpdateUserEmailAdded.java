package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserEmailAdded extends Update {

    public static final int HEADER = 0x60;
    public static UpdateUserEmailAdded fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserEmailAdded.class, data);
    }

    private int uid;
    private int emailId;

    public UpdateUserEmailAdded(int uid, int emailId) {
        this.uid = uid;
        this.emailId = emailId;
    }

    public UpdateUserEmailAdded() {

    }

    public int getUid() {
        return this.uid;
    }

    public int getEmailId() {
        return this.emailId;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.emailId = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeInt(2, this.emailId);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
