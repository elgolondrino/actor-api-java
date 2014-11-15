package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class OutPeer extends BserObject {

    private PeerType type;
    private int id;
    private long accessHash;

    public OutPeer(PeerType type, int id, long accessHash) {
        this.type = type;
        this.id = id;
        this.accessHash = accessHash;
    }

    public OutPeer() {

    }

    public PeerType getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.type = PeerType.parse(values.getInt(1));
        this.id = values.getInt(2);
        this.accessHash = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.type == null) {
            throw new IOException();
        }
        writer.writeInt(1, this.type.getValue());
        writer.writeInt(2, this.id);
        writer.writeLong(3, this.accessHash);
    }

}
