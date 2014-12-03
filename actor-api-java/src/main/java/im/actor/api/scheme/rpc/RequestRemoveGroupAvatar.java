package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestRemoveGroupAvatar extends Request<ResponseSeqDate> {

    public static final int HEADER = 0x65;
    public static RequestRemoveGroupAvatar fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestRemoveGroupAvatar.class, data);
    }

    private GroupOutPeer groupPeer;
    private long rid;

    public RequestRemoveGroupAvatar(GroupOutPeer groupPeer, long rid) {
        this.groupPeer = groupPeer;
        this.rid = rid;
    }

    public RequestRemoveGroupAvatar() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public long getRid() {
        return this.rid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.rid = values.getLong(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeLong(4, this.rid);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
