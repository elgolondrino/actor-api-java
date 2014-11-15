package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSearchContacts extends Request<ResponseSearchContacts> {

    public static final int HEADER = 0x70;
    public static RequestSearchContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSearchContacts.class, data);
    }

    private String request;

    public RequestSearchContacts(String request) {
        this.request = request;
    }

    public RequestSearchContacts() {

    }

    public String getRequest() {
        return this.request;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.request = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.request == null) {
            throw new IOException();
        }
        writer.writeString(1, this.request);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
