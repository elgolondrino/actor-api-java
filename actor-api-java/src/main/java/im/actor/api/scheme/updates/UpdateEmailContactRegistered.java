package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateEmailContactRegistered extends Update {

    public static final int HEADER = 0x78;
    public static UpdateEmailContactRegistered fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateEmailContactRegistered.class, data);
    }

    private int emailId;
    private int uid;

    public UpdateEmailContactRegistered(int emailId, int uid) {
        this.emailId = emailId;
        this.uid = uid;
    }

    public UpdateEmailContactRegistered() {

    }

    public int getEmailId() {
        return this.emailId;
    }

    public int getUid() {
        return this.uid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.emailId = values.getInt(1);
        this.uid = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.emailId);
        writer.writeInt(2, this.uid);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
