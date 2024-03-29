package im.actor.api.scheme.base;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class WeakUpdate extends RpcScope {

    public static final int HEADER = 0x1a;
    public static WeakUpdate fromBytes(byte[] data) throws IOException {
        return Bser.parse(WeakUpdate.class, data);
    }

    private long date;
    private int updateHeader;
    private byte[] update;

    public WeakUpdate(long date, int updateHeader, byte[] update) {
        this.date = date;
        this.updateHeader = updateHeader;
        this.update = update;
    }

    public WeakUpdate() {

    }

    public long getDate() {
        return this.date;
    }

    public int getUpdateHeader() {
        return this.updateHeader;
    }

    public byte[] getUpdate() {
        return this.update;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.date = values.getLong(1);
        this.updateHeader = values.getInt(2);
        this.update = values.getBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.date);
        writer.writeInt(2, this.updateHeader);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.update);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
