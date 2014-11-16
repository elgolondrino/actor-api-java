package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestRegisterGooglePush extends Request<ResponseVoid> {

    public static final int HEADER = 0x33;
    public static RequestRegisterGooglePush fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestRegisterGooglePush.class, data);
    }

    private long projectId;
    private String token;

    public RequestRegisterGooglePush(long projectId, String token) {
        this.projectId = projectId;
        this.token = token;
    }

    public RequestRegisterGooglePush() {

    }

    public long getProjectId() {
        return this.projectId;
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.projectId = values.getLong(1);
        this.token = values.getString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.projectId);
        if (this.token == null) {
            throw new IOException();
        }
        writer.writeString(2, this.token);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
