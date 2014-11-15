package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseCreateGroup extends Response {

    public static final int HEADER = 0x42;
    public static ResponseCreateGroup fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseCreateGroup.class, data);
    }

    private GroupOutPeer groupPeer;
    private int seq;
    private byte[] state;
    private int users;

    public ResponseCreateGroup(GroupOutPeer groupPeer, int seq, byte[] state, int users) {
        this.groupPeer = groupPeer;
        this.seq = seq;
        this.state = state;
        this.users = users;
    }

    public ResponseCreateGroup() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public int getSeq() {
        return this.seq;
    }

    public byte[] getState() {
        return this.state;
    }

    public int getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.seq = values.getInt(3);
        this.state = values.getBytes(4);
        this.users = values.getInt(5);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeInt(3, this.seq);
        if (this.state == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.state);
        writer.writeInt(5, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
