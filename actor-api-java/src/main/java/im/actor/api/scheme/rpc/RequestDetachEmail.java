package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestDetachEmail extends Request<ResponseSeq> {

    public static final int HEADER = 0x7b;
    public static RequestDetachEmail fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestDetachEmail.class, data);
    }

    private int email;
    private long accessHash;

    public RequestDetachEmail(int email, long accessHash) {
        this.email = email;
        this.accessHash = accessHash;
    }

    public RequestDetachEmail() {

    }

    public int getEmail() {
        return this.email;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.email = values.getInt(1);
        this.accessHash = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.email);
        writer.writeLong(2, this.accessHash);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
