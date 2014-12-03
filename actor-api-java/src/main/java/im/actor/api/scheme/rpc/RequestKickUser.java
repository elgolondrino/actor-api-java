package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestKickUser extends Request<ResponseSeqDate> {

    public static final int HEADER = 0x47;
    public static RequestKickUser fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestKickUser.class, data);
    }

    private GroupOutPeer groupPeer;
    private long rid;
    private UserOutPeer user;

    public RequestKickUser(GroupOutPeer groupPeer, long rid, UserOutPeer user) {
        this.groupPeer = groupPeer;
        this.rid = rid;
        this.user = user;
    }

    public RequestKickUser() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public long getRid() {
        return this.rid;
    }

    public UserOutPeer getUser() {
        return this.user;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.rid = values.getLong(4);
        this.user = values.getObj(3, UserOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeLong(4, this.rid);
        if (this.user == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.user);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
