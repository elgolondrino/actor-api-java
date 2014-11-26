package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseImportContacts extends Response {

    public static final int HEADER = 0x8;
    public static ResponseImportContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseImportContacts.class, data);
    }

    private List<User> users;
    private int seq;
    private byte[] state;

    public ResponseImportContacts(List<User> users, int seq, byte[] state) {
        this.users = users;
        this.seq = seq;
        this.state = state;
    }

    public ResponseImportContacts() {

    }

    public List<User> getUsers() {
        return this.users;
    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.users = values.getRepeatedObj(1, User.class);
        this.seq = values.getInt(2);
        this.state = values.getBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.users);
        writer.writeInt(2, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.state);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
