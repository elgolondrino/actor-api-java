package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditGroupAvatar extends Request<ResponseEditGroupAvatar> {

    public static final int HEADER = 0x56;
    public static RequestEditGroupAvatar fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditGroupAvatar.class, data);
    }

    private GroupOutPeer groupPeer;
    private long rid;
    private FileLocation fileLocation;

    public RequestEditGroupAvatar(GroupOutPeer groupPeer, long rid, FileLocation fileLocation) {
        this.groupPeer = groupPeer;
        this.rid = rid;
        this.fileLocation = fileLocation;
    }

    public RequestEditGroupAvatar() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public long getRid() {
        return this.rid;
    }

    public FileLocation getFileLocation() {
        return this.fileLocation;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.rid = values.getLong(4);
        this.fileLocation = values.getObj(3, FileLocation.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeLong(4, this.rid);
        if (this.fileLocation == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.fileLocation);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
