package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateTyping extends Update {

    public static final int HEADER = 0x6;
    public static UpdateTyping fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateTyping.class, data);
    }

    private Peer peer;
    private int uid;
    private int typingType;

    public UpdateTyping(Peer peer, int uid, int typingType) {
        this.peer = peer;
        this.uid = uid;
        this.typingType = typingType;
    }

    public UpdateTyping() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getUid() {
        return this.uid;
    }

    public int getTypingType() {
        return this.typingType;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.uid = values.getInt(2);
        this.typingType = values.getInt(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeInt(2, this.uid);
        writer.writeInt(3, this.typingType);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
