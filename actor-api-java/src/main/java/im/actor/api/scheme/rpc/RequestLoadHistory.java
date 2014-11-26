package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestLoadHistory extends Request<ResponseLoadHistory> {

    public static final int HEADER = 0x76;
    public static RequestLoadHistory fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestLoadHistory.class, data);
    }

    private OutPeer peer;
    private long startDate;
    private int limit;

    public RequestLoadHistory(OutPeer peer, long startDate, int limit) {
        this.peer = peer;
        this.startDate = startDate;
        this.limit = limit;
    }

    public RequestLoadHistory() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public long getStartDate() {
        return this.startDate;
    }

    public int getLimit() {
        return this.limit;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.startDate = values.getLong(3);
        this.limit = values.getInt(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.startDate);
        writer.writeInt(4, this.limit);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
