package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestUploadPart extends Request<ResponseVoid> {

    public static final int HEADER = 0x14;
    public static RequestUploadPart fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestUploadPart.class, data);
    }

    private UploadConfig config;
    private int blockIndex;
    private byte[] payload;

    public RequestUploadPart(UploadConfig config, int blockIndex, byte[] payload) {
        this.config = config;
        this.blockIndex = blockIndex;
        this.payload = payload;
    }

    public RequestUploadPart() {

    }

    public UploadConfig getConfig() {
        return this.config;
    }

    public int getBlockIndex() {
        return this.blockIndex;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.config = values.getObj(1, UploadConfig.class);
        this.blockIndex = values.getInt(2);
        this.payload = values.getBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.config == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.config);
        writer.writeInt(2, this.blockIndex);
        if (this.payload == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.payload);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
