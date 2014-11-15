package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestRemoveAuth extends Request<ResponseVoid> {

    public static final int HEADER = 0x52;
    public static RequestRemoveAuth fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestRemoveAuth.class, data);
    }

    private int id;

    public RequestRemoveAuth(int id) {
        this.id = id;
    }

    public RequestRemoveAuth() {

    }

    public int getId() {
        return this.id;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
