package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileLocation extends BserObject {

    private long fileId;
    private long accessHash;

    public FileLocation(long fileId, long accessHash) {
        this.fileId = fileId;
        this.accessHash = accessHash;
    }

    public FileLocation() {

    }

    public long getFileId() {
        return this.fileId;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.fileId = values.getLong(1);
        this.accessHash = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.fileId);
        writer.writeLong(2, this.accessHash);
    }

}
