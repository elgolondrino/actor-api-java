package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseVoid extends Response {

    public static final int HEADER = 0x32;
    public static ResponseVoid fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseVoid.class, data);
    }


    public ResponseVoid() {

    }

    @Override
    public void parse(BserValues values) throws IOException {
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
