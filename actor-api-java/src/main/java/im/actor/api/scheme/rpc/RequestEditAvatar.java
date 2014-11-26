package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditAvatar extends Request<ResponseEditAvatar> {

    public static final int HEADER = 0x1f;
    public static RequestEditAvatar fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditAvatar.class, data);
    }

    private FileLocation fileLocation;

    public RequestEditAvatar(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    public RequestEditAvatar() {

    }

    public FileLocation getFileLocation() {
        return this.fileLocation;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.fileLocation = values.getObj(1, FileLocation.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.fileLocation == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.fileLocation);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
