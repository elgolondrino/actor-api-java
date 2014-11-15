package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSubscribeToGrouOnline extends Request<ResponseVoid> {

    public static final int HEADER = 0x4a;
    public static RequestSubscribeToGrouOnline fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSubscribeToGrouOnline.class, data);
    }

    private List<GroupOutPeer> groups;

    public RequestSubscribeToGrouOnline(List<GroupOutPeer> groups) {
        this.groups = groups;
    }

    public RequestSubscribeToGrouOnline() {

    }

    public List<GroupOutPeer> getGroups() {
        return this.groups;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.groups = values.getRepeatedObj(1, GroupOutPeer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.groups);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
