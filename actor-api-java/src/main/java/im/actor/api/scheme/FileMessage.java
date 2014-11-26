package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileMessage extends BserObject {

    private long fileId;
    private long accessHash;
    private int fileSize;
    private String name;
    private String mimeType;
    private FastThumb thumb;
    private int extType;
    private byte[] ext;

    public FileMessage(long fileId, long accessHash, int fileSize, String name, String mimeType, FastThumb thumb, int extType, byte[] ext) {
        this.fileId = fileId;
        this.accessHash = accessHash;
        this.fileSize = fileSize;
        this.name = name;
        this.mimeType = mimeType;
        this.thumb = thumb;
        this.extType = extType;
        this.ext = ext;
    }

    public FileMessage() {

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

    public String getName() {
        return this.name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public FastThumb getThumb() {
        return this.thumb;
    }

    public int getExtType() {
        return this.extType;
    }

    public byte[] getExt() {
        return this.ext;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.fileId = values.getLong(1);
        this.accessHash = values.getLong(2);
        this.fileSize = values.getInt(3);
        this.name = values.getString(4);
        this.mimeType = values.getString(5);
        this.thumb = values.optObj(6, FastThumb.class);
        this.extType = values.getInt(7);
        this.ext = values.optBytes(8);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.fileId);
        writer.writeLong(2, this.accessHash);
        writer.writeInt(3, this.fileSize);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(4, this.name);
        if (this.mimeType == null) {
            throw new IOException();
        }
        writer.writeString(5, this.mimeType);
        if (this.thumb != null) {
            writer.writeObject(6, this.thumb);
        }
        writer.writeInt(7, this.extType);
        if (this.ext != null) {
            writer.writeBytes(8, this.ext);
        }
    }

}
