package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestDeleteChat extends Request<ResponseSeq> {

    public static final int HEADER = 0x64;
    public static RequestDeleteChat fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestDeleteChat.class, data);
    }

    private OutPeer peer;

    public RequestDeleteChat(OutPeer peer) {
        this.peer = peer;
    }

    public RequestDeleteChat() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
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
