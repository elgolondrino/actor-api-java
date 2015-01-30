package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSendEmailCode extends Request<ResponseVoid> {

    public static final int HEADER = 0x78;
    public static RequestSendEmailCode fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSendEmailCode.class, data);
    }

    private String email;
    private String description;

    public RequestSendEmailCode(String email, String description) {
        this.email = email;
        this.description = description;
    }

    public RequestSendEmailCode() {

    }

    public String getEmail() {
        return this.email;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.email = values.getString(1);
        this.description = values.optString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.email == null) {
            throw new IOException();
        }
        writer.writeString(1, this.email);
        if (this.description != null) {
            writer.writeString(2, this.description);
        }
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
