package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class PlainFileLocation extends BserObject {

    private long fileId;
    private long accessHash;
    private int fileSize;
    private EncryptionType encryptionType;
    private int encryptedFileSize;
    private byte[] encryptionKey;

    public PlainFileLocation(long fileId, long accessHash, int fileSize, EncryptionType encryptionType, int encryptedFileSize, byte[] encryptionKey) {
        this.fileId = fileId;
        this.accessHash = accessHash;
        this.fileSize = fileSize;
        this.encryptionType = encryptionType;
        this.encryptedFileSize = encryptedFileSize;
        this.encryptionKey = encryptionKey;
    }

    public PlainFileLocation() {

    }

    public long getFileId() {
        return this.fileId;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public EncryptionType getEncryptionType() {
        return this.encryptionType;
    }

    public int getEncryptedFileSize() {
        return this.encryptedFileSize;
    }

    public byte[] getEncryptionKey() {
        return this.encryptionKey;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.fileId = values.getLong(1);
        this.accessHash = values.getLong(2);
        this.fileSize = values.getInt(3);
        this.encryptionType = EncryptionType.parse(values.getInt(4));
        this.encryptedFileSize = values.getInt(5);
        this.encryptionKey = values.getBytes(6);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.fileId);
        writer.writeLong(2, this.accessHash);
        writer.writeInt(3, this.fileSize);
        if (this.encryptionType == null) {
            throw new IOException();
        }
        writer.writeInt(4, this.encryptionType.getValue());
        writer.writeInt(5, this.encryptedFileSize);
        if (this.encryptionKey == null) {
            throw new IOException();
        }
        writer.writeBytes(6, this.encryptionKey);
    }

}
