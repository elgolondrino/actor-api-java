package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class EncryptedAesKey extends BserObject {

    private long keyHash;
    private byte[] aesEncryptedKey;

    public EncryptedAesKey(long keyHash, byte[] aesEncryptedKey) {
        this.keyHash = keyHash;
        this.aesEncryptedKey = aesEncryptedKey;
    }

    public EncryptedAesKey() {

    }

    public long getKeyHash() {
        return this.keyHash;
    }

    public byte[] getAesEncryptedKey() {
        return this.aesEncryptedKey;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.keyHash = values.getLong(1);
        this.aesEncryptedKey = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.keyHash);
        if (this.aesEncryptedKey == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.aesEncryptedKey);
    }

}
