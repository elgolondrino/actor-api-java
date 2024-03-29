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
    private long date;

    public RequestMessageReceived(OutPeer peer, long date) {
        this.peer = peer;
        this.date = date;
    }

    public RequestMessageReceived() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.date);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
