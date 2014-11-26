package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateMessageReadByMe extends Update {

    public static final int HEADER = 0x32;
    public static UpdateMessageReadByMe fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateMessageReadByMe.class, data);
    }

    private Peer peer;
    private long startDate;

    public UpdateMessageReadByMe(Peer peer, long startDate) {
        this.peer = peer;
        this.startDate = startDate;
    }

    public UpdateMessageReadByMe() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public long getStartDate() {
        return this.startDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.startDate = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(2, this.startDate);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
