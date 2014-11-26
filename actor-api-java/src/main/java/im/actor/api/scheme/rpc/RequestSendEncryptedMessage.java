package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSendEncryptedMessage extends Request<ResponseSeqDate> {

    public static final int HEADER = 0xe;
    public static RequestSendEncryptedMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSendEncryptedMessage.class, data);
    }

    private OutPeer peer;
    private long rid;
    private byte[] encryptedMessage;
    private List<EncryptedAesKey> keys;
    private List<EncryptedAesKey> ownKeys;

    public RequestSendEncryptedMessage(OutPeer peer, long rid, byte[] encryptedMessage, List<EncryptedAesKey> keys, List<EncryptedAesKey> ownKeys) {
        this.peer = peer;
        this.rid = rid;
        this.encryptedMessage = encryptedMessage;
        this.keys = keys;
        this.ownKeys = ownKeys;
    }

    public RequestSendEncryptedMessage() {

    }

    public OutPeer getPeer() {
        return this.peer;
    }

    public long getRid() {
        return this.rid;
    }

    public byte[] getEncryptedMessage() {
        return this.encryptedMessage;
    }

    public List<EncryptedAesKey> getKeys() {
        return this.keys;
    }

    public List<EncryptedAesKey> getOwnKeys() {
        return this.ownKeys;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, OutPeer.class);
        this.rid = values.getLong(3);
        this.encryptedMessage = values.getBytes(4);
        this.keys = values.getRepeatedObj(5, EncryptedAesKey.class);
        this.ownKeys = values.getRepeatedObj(6, EncryptedAesKey.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.rid);
        if (this.encryptedMessage == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.encryptedMessage);
        writer.writeRepeatedObj(5, this.keys);
        writer.writeRepeatedObj(6, this.ownKeys);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
