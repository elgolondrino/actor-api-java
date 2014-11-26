package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateContactsRemoved extends Update {

    public static final int HEADER = 0x29;
    public static UpdateContactsRemoved fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateContactsRemoved.class, data);
    }

    private List<Integer> uids;

    public UpdateContactsRemoved(List<Integer> uids) {
        this.uids = uids;
    }

    public UpdateContactsRemoved() {

    }

    public List<Integer> getUids() {
        return this.uids;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uids = values.getRepeatedInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedInt(1, this.uids);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
