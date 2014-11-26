package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetPublicKeys extends Response {

    public static final int HEADER = 0x18;
    public static ResponseGetPublicKeys fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetPublicKeys.class, data);
    }

    private List<PublicKey> keys;

    public ResponseGetPublicKeys(List<PublicKey> keys) {
        this.keys = keys;
    }

    public ResponseGetPublicKeys() {

    }

    public List<PublicKey> getKeys() {
        return this.keys;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.keys = values.getRepeatedObj(1, PublicKey.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.keys);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
