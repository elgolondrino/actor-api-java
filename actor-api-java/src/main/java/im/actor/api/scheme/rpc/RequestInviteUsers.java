package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestInviteUsers extends Request<ResponseSeq> {

    public static final int HEADER = 0x45;
    public static RequestInviteUsers fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestInviteUsers.class, data);
    }

    private GroupOutPeer groupPeer;
    private List<UserOutPeer> users;

    public RequestInviteUsers(GroupOutPeer groupPeer, List<UserOutPeer> users) {
        this.groupPeer = groupPeer;
        this.users = users;
    }

    public RequestInviteUsers() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public List<UserOutPeer> getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.users = values.getRepeatedObj(3, UserOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeRepeatedObj(3, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
