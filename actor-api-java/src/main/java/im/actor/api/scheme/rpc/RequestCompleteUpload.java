package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestCompleteUpload extends Request<ResponseCompleteUpload> {

    public static final int HEADER = 0x16;
    public static RequestCompleteUpload fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestCompleteUpload.class, data);
    }

    private UploadConfig config;
    private int blocksCount;
    private long crc32;

    public RequestCompleteUpload(UploadConfig config, int blocksCount, long crc32) {
        this.config = config;
        this.blocksCount = blocksCount;
        this.crc32 = crc32;
    }

    public RequestCompleteUpload() {

    }

    public UploadConfig getConfig() {
        return this.config;
    }

    public int getBlocksCount() {
        return this.blocksCount;
    }

    public long getCrc32() {
        return this.crc32;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.config = values.getObj(1, UploadConfig.class);
        this.blocksCount = values.getInt(2);
        this.crc32 = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.config == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.config);
        writer.writeInt(2, this.blocksCount);
        writer.writeLong(3, this.crc32);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
