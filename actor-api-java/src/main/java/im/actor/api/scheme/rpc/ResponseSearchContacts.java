package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseSearchContacts extends Response {

    public static final int HEADER = 0x71;
    public static ResponseSearchContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseSearchContacts.class, data);
    }

    private List<User> users;

    public ResponseSearchContacts(List<User> users) {
        this.users = users;
    }

    public ResponseSearchContacts() {

    }

    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.users = values.getRepeatedObj(1, User.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
