package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateEncryptedMessage extends Update {

    public static final int HEADER = 0x1;
    public static UpdateEncryptedMessage fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateEncryptedMessage.class, data);
    }

    private Peer peer;
    private int senderUid;
    private long date;
    private long keyHash;
    private byte[] aesEncryptedKey;
    private byte[] message;

    public UpdateEncryptedMessage(Peer peer, int senderUid, long date, long keyHash, byte[] aesEncryptedKey, byte[] message) {
        this.peer = peer;
        this.senderUid = senderUid;
        this.date = date;
        this.keyHash = keyHash;
        this.aesEncryptedKey = aesEncryptedKey;
        this.message = message;
    }

    public UpdateEncryptedMessage() {

    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getSenderUid() {
        return this.senderUid;
    }

    public long getDate() {
        return this.date;
    }

    public long getKeyHash() {
        return this.keyHash;
    }

    public byte[] getAesEncryptedKey() {
        return this.aesEncryptedKey;
    }

    public byte[] getMessage() {
        return this.message;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, Peer.class);
        this.senderUid = values.getInt(2);
        this.date = values.getLong(6);
        this.keyHash = values.getLong(3);
        this.aesEncryptedKey = values.getBytes(4);
        this.message = values.getBytes(5);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeInt(2, this.senderUid);
        writer.writeLong(6, this.date);
        writer.writeLong(3, this.keyHash);
        if (this.aesEncryptedKey == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.aesEncryptedKey);
        if (this.message == null) {
            throw new IOException();
        }
        writer.writeBytes(5, this.message);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
