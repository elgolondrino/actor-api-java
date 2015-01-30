package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserEmailRemoved extends Update {

    public static final int HEADER = 0x61;
    public static UpdateUserEmailRemoved fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserEmailRemoved.class, data);
    }

    private int uid;
    private int emailId;

    public UpdateUserEmailRemoved(int uid, int emailId) {
        this.uid = uid;
        this.emailId = emailId;
    }

    public UpdateUserEmailRemoved() {

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
