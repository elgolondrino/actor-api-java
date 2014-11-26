package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseGetAuthSessions extends Response {

    public static final int HEADER = 0x51;
    public static ResponseGetAuthSessions fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseGetAuthSessions.class, data);
    }

    private List<AuthSession> userAuths;

    public ResponseGetAuthSessions(List<AuthSession> userAuths) {
        this.userAuths = userAuths;
    }

    public ResponseGetAuthSessions() {

    }

    public List<AuthSession> getUserAuths() {
        return this.userAuths;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.userAuths = values.getRepeatedObj(1, AuthSession.class);
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
