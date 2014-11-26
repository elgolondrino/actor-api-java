package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateGroupMembersUpdate extends Update {

    public static final int HEADER = 0x2c;
    public static UpdateGroupMembersUpdate fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateGroupMembersUpdate.class, data);
    }

    private int groupId;
    private List<Integer> members;

    public UpdateGroupMembersUpdate(int groupId, List<Integer> members) {
        this.groupId = groupId;
        this.members = members;
    }

    public UpdateGroupMembersUpdate() {

    }

    public int getGroupId() {
        return this.groupId;
    }

    public List<Integer> getMembers() {
        return this.members;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupId = values.getInt(1);
        this.members = values.getRepeatedInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.groupId);
        writer.writeRepeatedInt(2, this.members);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
