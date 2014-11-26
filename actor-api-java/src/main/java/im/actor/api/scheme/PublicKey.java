package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class PublicKey extends BserObject {

    private int uid;
    private long keyHash;
    private byte[] key;

    public PublicKey(int uid, long keyHash, byte[] key) {
        this.uid = uid;
        this.keyHash = keyHash;
        this.key = key;
    }

    public PublicKey() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getKeyHash() {
        return this.keyHash;
    }

    public byte[] getKey() {
        return this.key;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.keyHash = values.getLong(2);
        this.key = values.getBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.keyHash);
        if (this.key == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.key);
    }

}
