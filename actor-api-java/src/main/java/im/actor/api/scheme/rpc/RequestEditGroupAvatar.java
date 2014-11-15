package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditGroupAvatar extends Request<ResponseAvatarChanged> {

    public static final int HEADER = 0x56;
    public static RequestEditGroupAvatar fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditGroupAvatar.class, data);
    }

    private GroupOutPeer groupPeer;
    private FileLocation fileLocation;

    public RequestEditGroupAvatar(GroupOutPeer groupPeer, FileLocation fileLocation) {
        this.groupPeer = groupPeer;
        this.fileLocation = fileLocation;
    }

    public RequestEditGroupAvatar() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public FileLocation getFileLocation() {
        return this.fileLocation;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.fileLocation = values.getObj(3, FileLocation.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
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
