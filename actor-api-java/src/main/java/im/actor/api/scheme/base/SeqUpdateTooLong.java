package im.actor.api.scheme.base;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class SeqUpdateTooLong extends RpcScope {

    public static final int HEADER = 0x19;
    public static SeqUpdateTooLong fromBytes(byte[] data) throws IOException {
        return Bser.parse(SeqUpdateTooLong.class, data);
    }


    public SeqUpdateTooLong() {

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
