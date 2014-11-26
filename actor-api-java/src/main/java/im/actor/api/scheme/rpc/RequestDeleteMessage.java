package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestDeleteMessage extends Request<ResponseVoid> {

    public static final int HEADER = 0x62;
    public static RequestDeleteMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestDeleteMessage.class, data);
    }

    private OutPeer peer;
    private List<Long> rids;

    public RequestDeleteMessage(OutPeer peer, List<Long> rids) {
        this.peer = peer;
        this.rids = rids;
    }

    public RequestDeleteMessage() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public List<Long> getRids() {
        return this.rids;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.rids = values.getRepeatedLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeRepeatedLong(3, this.rids);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
