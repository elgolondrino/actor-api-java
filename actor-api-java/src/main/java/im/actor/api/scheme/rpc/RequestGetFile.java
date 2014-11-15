package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestGetFile extends Request<ResponseGetFile> {

    public static final int HEADER = 0x10;
    public static RequestGetFile fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestGetFile.class, data);
    }

    private FileLocation fileLocation;
    private int offset;
    private int limit;

    public RequestGetFile(FileLocation fileLocation, int offset, int limit) {
        this.fileLocation = fileLocation;
        this.offset = offset;
        this.limit = limit;
    }

    public RequestGetFile() {

    }

    public FileLocation getFileLocation() {
        return this.fileLocation;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLimit() {
        return this.limit;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.fileLocation = values.getObj(1, FileLocation.class);
        this.offset = values.getInt(2);
        this.limit = values.getInt(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.fileLocation == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.fileLocation);
        writer.writeInt(2, this.offset);
        writer.writeInt(3, this.limit);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
