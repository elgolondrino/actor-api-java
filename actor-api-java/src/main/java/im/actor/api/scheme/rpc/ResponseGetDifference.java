package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetDifference extends Response {

    public static final int HEADER = 0xc;
    public static ResponseGetDifference fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetDifference.class, data);
    }

    private int seq;
    private byte[] state;
    private List<User> users;
    private List<Group> groups;
    private List<Phone> phones;
    private List<Email> emails;
    private List<DifferenceUpdate> updates;
    private boolean needMore;

    public ResponseGetDifference(int seq, byte[] state, List<User> users, List<Group> groups, List<Phone> phones, List<Email> emails, List<DifferenceUpdate> updates, boolean needMore) {
        this.seq = seq;
        this.state = state;
        this.users = users;
        this.groups = groups;
        this.phones = phones;
        this.emails = emails;
        this.updates = updates;
        this.needMore = needMore;
    }

    public ResponseGetDifference() {

    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public List<Phone> getPhones() {
        return this.phones;
    }

    public List<Email> getEmails() {
        return this.emails;
    }

    public List<DifferenceUpdate> getUpdates() {
        return this.updates;
    }

    public boolean needMore() {
        return this.needMore;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
        this.users = values.getRepeatedObj(3, User.class);
        this.groups = values.getRepeatedObj(6, Group.class);
        this.phones = values.getRepeatedObj(7, Phone.class);
        this.emails = values.getRepeatedObj(8, Email.class);
        this.updates = values.getRepeatedObj(4, DifferenceUpdate.class);
        this.needMore = values.getBool(5);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
        writer.writeRepeatedObj(3, this.users);
        writer.writeRepeatedObj(6, this.groups);
        writer.writeRepeatedObj(7, this.phones);
        writer.writeRepeatedObj(8, this.emails);
        writer.writeRepeatedObj(4, this.updates);
        writer.writeBool(5, this.needMore);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
