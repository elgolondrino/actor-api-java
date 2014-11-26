package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class ServiceExUserKicked extends BserObject {

    private int kickedUid;

    public ServiceExUserKicked(int kickedUid) {
        this.kickedUid = kickedUid;
    }

    public ServiceExUserKicked() {

    }

    public int getKickedUid() {
        return this.kickedUid;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.kickedUid = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.kickedUid);
    }

}
