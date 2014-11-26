package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestCreateGroup extends Request<ResponseCreateGroup> {

    public static final int HEADER = 0x41;
    public static RequestCreateGroup fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestCreateGroup.class, data);
    }

    private long rid;
    private String title;
    private List<UserOutPeer> users;

    public RequestCreateGroup(long rid, String title, List<UserOutPeer> users) {
        this.rid = rid;
        this.title = title;
        this.users = users;
    }

    public RequestCreateGroup() {

    }

    public long getRid() {
        return this.rid;
    }

    public String getTitle() {
        return this.title;
    }

    public List<UserOutPeer> getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.rid = values.getLong(1);
        this.title = values.getString(2);
        this.users = values.getRepeatedObj(3, UserOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.rid);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(2, this.title);
        writer.writeRepeatedObj(3, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
