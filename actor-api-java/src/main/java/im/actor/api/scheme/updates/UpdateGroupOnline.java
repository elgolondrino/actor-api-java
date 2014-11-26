package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupOnline extends Update {

    public static final int HEADER = 0x21;
    public static UpdateGroupOnline fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupOnline.class, data);
    }

    private int groupId;
    private int count;

    public UpdateGroupOnline(int groupId, int count) {
        this.groupId = groupId;
        this.count = count;
    }

    public UpdateGroupOnline() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.count = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeInt(2, this.count);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
