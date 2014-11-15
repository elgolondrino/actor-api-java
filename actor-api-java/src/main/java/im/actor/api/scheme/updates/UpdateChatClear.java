package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateChatClear extends Update {

    public static final int HEADER = 0x2f;
    public static UpdateChatClear fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateChatClear.class, data);
    }

    private Peer peer;

    public UpdateChatClear(Peer peer) {
        this.peer = peer;
    }

    public UpdateChatClear() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
