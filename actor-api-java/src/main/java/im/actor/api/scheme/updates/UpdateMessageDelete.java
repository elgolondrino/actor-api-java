package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateMessageDelete extends Update {

    public static final int HEADER = 0x2e;
    public static UpdateMessageDelete fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateMessageDelete.class, data);
    }

    private Peer peer;
    private List<Long> rids;

    public UpdateMessageDelete(Peer peer, List<Long> rids) {
        this.peer = peer;
        this.rids = rids;
    }

    public UpdateMessageDelete() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public List<Long> getRids() {
        return this.rids;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.rids = values.getRepeatedLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeRepeatedLong(2, this.rids);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
