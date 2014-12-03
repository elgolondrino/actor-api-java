package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditGroupTitle extends Request<ResponseSeqDate> {

    public static final int HEADER = 0x55;
    public static RequestEditGroupTitle fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditGroupTitle.class, data);
    }

    private GroupOutPeer groupPeer;
    private long rid;
    private String title;

    public RequestEditGroupTitle(GroupOutPeer groupPeer, long rid, String title) {
        this.groupPeer = groupPeer;
        this.rid = rid;
        this.title = title;
    }

    public RequestEditGroupTitle() {

    }

    public GroupOutPeer getGroupPeer() {
        return this.groupPeer;
    }

    public long getRid() {
        return this.rid;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groupPeer = values.getObj(1, GroupOutPeer.class);
        this.rid = values.getLong(4);
        this.title = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.groupPeer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.groupPeer);
        writer.writeLong(4, this.rid);
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
