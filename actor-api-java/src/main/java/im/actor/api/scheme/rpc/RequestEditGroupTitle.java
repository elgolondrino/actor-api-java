package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditGroupTitle extends Request<ResponseSeq> {

    public static final int HEADER = 0x55;
    public static RequestEditGroupTitle fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditGroupTitle.class, data);
    }

    private GroupOutPeer groupPeer;
    private String title;

    public RequestEditGroupTitle(GroupOutPeer groupPeer, String title) {
        this.groupPeer = groupPeer;
        this.title = title;
    }

    public RequestEditGroupTitle() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.title = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(3, this.title);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
