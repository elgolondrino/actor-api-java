package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class DifferenceUpdate extends BserObject {

    private int updateId;
    private byte[] update;

    public DifferenceUpdate(int updateId, byte[] update) {
        this.updateId = updateId;
        this.update = update;
    }

    public DifferenceUpdate() {

    }

    public int getUpdateId() {
        return this.updateId;
    }

    public byte[] getUpdate() {
        return this.update;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.updateId = values.getInt(1);
        this.update = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.updateId);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.update);
    }

}
