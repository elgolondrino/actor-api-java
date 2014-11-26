package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestLoadDialogs extends Request<ResponseLoadDialogs> {

    public static final int HEADER = 0x68;
    public static RequestLoadDialogs fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestLoadDialogs.class, data);
    }

    private long startDate;
    private int limit;

    public RequestLoadDialogs(long startDate, int limit) {
        this.startDate = startDate;
        this.limit = limit;
    }

    public RequestLoadDialogs() {

    }

    public long getStartDate() {
        return this.startDate;
    }

    public int getLimit() {
        return this.limit;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.startDate = values.getLong(1);
        this.limit = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.startDate);
        writer.writeInt(2, this.limit);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
