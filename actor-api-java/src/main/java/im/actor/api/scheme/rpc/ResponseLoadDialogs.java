package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseLoadDialogs extends Response {

    public static final int HEADER = 0x69;
    public static ResponseLoadDialogs fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseLoadDialogs.class, data);
    }

    private List<Group> groups;
    private List<User> users;
    private List<Dialog> dialogs;

    public ResponseLoadDialogs(List<Group> groups, List<User> users, List<Dialog> dialogs) {
        this.groups = groups;
        this.users = users;
        this.dialogs = dialogs;
    }

    public ResponseLoadDialogs() {

    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public List<Dialog> getDialogs() {
        return this.dialogs;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groups = values.getRepeatedObj(1, Group.class);
        this.users = values.getRepeatedObj(2, User.class);
        this.dialogs = values.getRepeatedObj(3, Dialog.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.groups);
        writer.writeRepeatedObj(2, this.users);
        writer.writeRepeatedObj(3, this.dialogs);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
