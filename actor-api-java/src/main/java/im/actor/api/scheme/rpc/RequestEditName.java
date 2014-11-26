package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestEditName extends Request<ResponseSeq> {

    public static final int HEADER = 0x35;
    public static RequestEditName fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestEditName.class, data);
    }

    private String name;

    public RequestEditName(String name) {
        this.name = name;
    }

    public RequestEditName() {

    }

    public String getName() {
        return this.name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.name = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(1, this.name);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
