package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateNewDevice extends Update {

    public static final int HEADER = 0x2;
    public static UpdateNewDevice fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateNewDevice.class, data);
    }

    private int uid;
    private long keyHash;
    private byte[] key;
    private long date;

    public UpdateNewDevice(int uid, long keyHash, byte[] key, long date) {
        this.uid = uid;
        this.keyHash = keyHash;
        this.key = key;
        this.date = date;
    }

    public UpdateNewDevice() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getKeyHash() {
        return this.keyHash;
    }

    public byte[] getKey() {
        return this.key;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.keyHash = values.getLong(2);
        this.key = values.optBytes(3);
        this.date = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.keyHash);
        if (this.key != null) {
            writer.writeBytes(3, this.key);
        }
        writer.writeLong(4, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
