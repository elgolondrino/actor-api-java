package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSetOnline extends Request<ResponseVoid> {

    public static final int HEADER = 0x1d;
    public static RequestSetOnline fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSetOnline.class, data);
    }

    private boolean isOnline;
    private long timeout;

    public RequestSetOnline(boolean isOnline, long timeout) {
        this.isOnline = isOnline;
        this.timeout = timeout;
    }

    public RequestSetOnline() {

    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.isOnline = values.getBool(1);
        this.timeout = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeBool(1, this.isOnline);
        writer.writeLong(2, this.timeout);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
