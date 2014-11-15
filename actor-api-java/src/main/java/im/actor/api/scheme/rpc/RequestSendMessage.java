package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSendMessage extends Request<ResponseMessageSent> {

    public static final int HEADER = 0x5c;
    public static RequestSendMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSendMessage.class, data);
    }

    private OutPeer peer;
    private long rid;
    private MessageContent message;

    public RequestSendMessage(OutPeer peer, long rid, MessageContent message) {
        this.peer = peer;
        this.rid = rid;
        this.message = message;
    }

    public RequestSendMessage() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public long getRid() {
        return this.rid;
    }

    public MessageContent getMessage() {
        return this.message;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.rid = values.getLong(3);
        this.message = values.getObj(4, MessageContent.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.rid);
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeObject(4, this.message);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
