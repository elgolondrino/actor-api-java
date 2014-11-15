package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestRegisterApplePush extends Request<ResponseVoid> {

    public static final int HEADER = 0x4c;
    public static RequestRegisterApplePush fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestRegisterApplePush.class, data);
    }

    private int apnsKey;
    private String token;

    public RequestRegisterApplePush(int apnsKey, String token) {
        this.apnsKey = apnsKey;
        this.token = token;
    }

    public RequestRegisterApplePush() {

    }

    public int getApnsKey() {
        return this.apnsKey;
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.apnsKey = values.getInt(1);
        this.token = values.getString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.apnsKey);
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
