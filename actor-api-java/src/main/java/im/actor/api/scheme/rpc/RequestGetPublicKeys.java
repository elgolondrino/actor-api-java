package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestGetPublicKeys extends Request<ResponseGetPublicKeys> {

    public static final int HEADER = 0x6;
    public static RequestGetPublicKeys fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestGetPublicKeys.class, data);
    }

    private List<PublicKeyRequest> keys;

    public RequestGetPublicKeys(List<PublicKeyRequest> keys) {
        this.keys = keys;
    }

    public RequestGetPublicKeys() {

    }

    public List<PublicKeyRequest> getKeys() {
        return this.keys;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.keys = values.getRepeatedObj(1, PublicKeyRequest.class);
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
