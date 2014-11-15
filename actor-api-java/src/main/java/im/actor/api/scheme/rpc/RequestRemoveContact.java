package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestRemoveContact extends Request<ResponseSeq> {

    public static final int HEADER = 0x59;
    public static RequestRemoveContact fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestRemoveContact.class, data);
    }

    private int uid;
    private long accessHash;

    public RequestRemoveContact(int uid, long accessHash) {
        this.uid = uid;
        this.accessHash = accessHash;
    }

    public RequestRemoveContact() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.accessHash = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.accessHash);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
