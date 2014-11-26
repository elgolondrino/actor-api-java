package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditUserLocalName extends Request<ResponseSeq> {

    public static final int HEADER = 0x60;
    public static RequestEditUserLocalName fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditUserLocalName.class, data);
    }

    private int uid;
    private long accessHash;
    private String name;

    public RequestEditUserLocalName(int uid, long accessHash, String name) {
        this.uid = uid;
        this.accessHash = accessHash;
        this.name = name;
    }

    public RequestEditUserLocalName() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.name = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.accessHash);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(3, this.name);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
