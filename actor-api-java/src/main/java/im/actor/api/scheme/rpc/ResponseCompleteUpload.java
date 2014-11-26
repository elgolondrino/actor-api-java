package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseCompleteUpload extends Response {

    public static final int HEADER = 0x17;
    public static ResponseCompleteUpload fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseCompleteUpload.class, data);
    }

    private FileLocation location;

    public ResponseCompleteUpload(FileLocation location) {
        this.location = location;
    }

    public ResponseCompleteUpload() {

    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.location = values.getObj(1, FileLocation.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.location == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.location);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
