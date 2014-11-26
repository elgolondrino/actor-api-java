package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestTyping extends Request<ResponseVoid> {

    public static final int HEADER = 0x1b;
    public static RequestTyping fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestTyping.class, data);
    }

    private OutPeer peer;
    private int typingType;

    public RequestTyping(OutPeer peer, int typingType) {
        this.peer = peer;
        this.typingType = typingType;
    }

    public RequestTyping() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public int getTypingType() {
        return this.typingType;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.typingType = values.getInt(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeInt(3, this.typingType);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
