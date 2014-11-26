package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class DifferenceUpdate extends BserObject {

    private int updateHeader;
    private byte[] update;

    public DifferenceUpdate(int updateHeader, byte[] update) {
        this.updateHeader = updateHeader;
        this.update = update;
    }

    public DifferenceUpdate() {

    }

    public int getUpdateHeader() {
        return this.updateHeader;
    }

    public byte[] getUpdate() {
        return this.update;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.updateHeader = values.getInt(1);
        this.update = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.updateHeader);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.update);
    }

}
