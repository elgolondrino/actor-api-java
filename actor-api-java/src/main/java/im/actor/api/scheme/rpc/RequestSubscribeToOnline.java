package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSubscribeToOnline extends Request<ResponseVoid> {

    public static final int HEADER = 0x20;
    public static RequestSubscribeToOnline fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSubscribeToOnline.class, data);
    }

    private List<UserOutPeer> users;

    public RequestSubscribeToOnline(List<UserOutPeer> users) {
        this.users = users;
    }

    public RequestSubscribeToOnline() {

    }

    public List<UserOutPeer> getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.users = values.getRepeatedObj(1, UserOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
