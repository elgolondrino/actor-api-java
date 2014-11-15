package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetContacts extends Response {

    public static final int HEADER = 0x58;
    public static ResponseGetContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetContacts.class, data);
    }

    private List<User> users;
    private boolean isNotChanged;

    public ResponseGetContacts(List<User> users, boolean isNotChanged) {
        this.users = users;
        this.isNotChanged = isNotChanged;
    }

    public ResponseGetContacts() {

    }

    public List<User> getUsers() {
        return this.users;
    }

    public boolean isNotChanged() {
        return this.isNotChanged;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.users = values.getRepeatedObj(1, User.class);
        this.isNotChanged = values.getBool(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.users);
        writer.writeBool(2, this.isNotChanged);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
