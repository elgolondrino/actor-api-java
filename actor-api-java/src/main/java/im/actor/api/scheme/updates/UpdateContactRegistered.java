package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateContactRegistered extends Update {

    public static final int HEADER = 0x5;
    public static UpdateContactRegistered fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateContactRegistered.class, data);
    }

    private int uid;
    private boolean isSilent;
    private long date;

    public UpdateContactRegistered(int uid, boolean isSilent, long date) {
        this.uid = uid;
        this.isSilent = isSilent;
        this.date = date;
    }

    public UpdateContactRegistered() {

    }

    public int getUid() {
        return this.uid;
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.isSilent = values.getBool(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeBool(2, this.isSilent);
        writer.writeLong(3, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
