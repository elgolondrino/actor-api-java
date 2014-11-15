package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestMessageReceived extends Request<ResponseVoid> {

    public static final int HEADER = 0x37;
    public static RequestMessageReceived fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestMessageReceived.class, data);
    }

    private OutPeer peer;
    private long rid;

    public RequestMessageReceived(OutPeer peer, long rid) {
        this.peer = peer;
        this.rid = rid;
    }

    public RequestMessageReceived() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public long getRid() {
        return this.rid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.rid = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.rid);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
