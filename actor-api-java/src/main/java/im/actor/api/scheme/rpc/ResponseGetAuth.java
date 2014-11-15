package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetAuth extends Response {

    public static final int HEADER = 0x51;
    public static ResponseGetAuth fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetAuth.class, data);
    }

    private List<AuthItem> userAuths;

    public ResponseGetAuth(List<AuthItem> userAuths) {
        this.userAuths = userAuths;
    }

    public ResponseGetAuth() {

    }

    public List<AuthItem> getUserAuths() {
        return this.userAuths;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userAuths = values.getRepeatedObj(1, AuthItem.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.userAuths);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
