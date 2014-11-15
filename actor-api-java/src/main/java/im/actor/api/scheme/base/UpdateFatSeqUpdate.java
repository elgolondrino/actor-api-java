package im.actor.api.scheme.base;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateFatSeqUpdate extends RpcScope {

    public static final int HEADER = 0x49;
    public static UpdateFatSeqUpdate fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateFatSeqUpdate.class, data);
    }

    private int seq;
    private byte[] state;
    private int updateHeader;
    private byte[] update;
    private List<User> users;
    private List<Group> groups;

    public UpdateFatSeqUpdate(int seq, byte[] state, int updateHeader, byte[] update, List<User> users, List<Group> groups) {
        this.seq = seq;
        this.state = state;
        this.updateHeader = updateHeader;
        this.update = update;
        this.users = users;
        this.groups = groups;
    }

    public UpdateFatSeqUpdate() {

    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    public int getUpdateHeader() {
        return this.updateHeader;
    }

    public byte[] getUpdate() {
        return this.update;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.seq = values.getInt(1);
        this.state = values.getBytes(2);
        this.updateHeader = values.getInt(3);
        this.update = values.getBytes(4);
        this.users = values.getRepeatedObj(5, User.class);
        this.groups = values.getRepeatedObj(6, Group.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.state);
        writer.writeInt(3, this.updateHeader);
        if (this.update == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.update);
        writer.writeRepeatedObj(5, this.users);
        writer.writeRepeatedObj(6, this.groups);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
