package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Peer extends BserObject {

    private PeerType type;
    private int id;

    public Peer(PeerType type, int id) {
        this.type = type;
        this.id = id;
    }

    public Peer() {

    }

    public PeerType getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.type = PeerType.parse(values.getInt(1));
        this.id = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.type == null) {
            throw new IOException();
        }
        writer.writeInt(1, this.type.getValue());
        writer.writeInt(2, this.id);
    }

}
