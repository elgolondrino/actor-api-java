package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestDeleteGroup extends Request<ResponseSeqDate> {

    public static final int HEADER = 0x61;
    public static RequestDeleteGroup fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestDeleteGroup.class, data);
    }

    private GroupOutPeer groupPeer;

    public RequestDeleteGroup(GroupOutPeer groupPeer) {
        this.groupPeer = groupPeer;
    }

    public RequestDeleteGroup() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
