package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseStartUpload extends Response {

    public static final int HEADER = 0x13;
    public static ResponseStartUpload fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseStartUpload.class, data);
    }

    private UploadConfig config;

    public ResponseStartUpload(UploadConfig config) {
        this.config = config;
    }

    public ResponseStartUpload() {

    }

    public UploadConfig getConfig() {
        return this.config;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.config = values.getObj(1, UploadConfig.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.config == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.config);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
