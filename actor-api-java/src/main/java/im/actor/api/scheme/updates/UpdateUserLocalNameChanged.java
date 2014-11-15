package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserLocalNameChanged extends Update {

    public static final int HEADER = 0x33;
    public static UpdateUserLocalNameChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserLocalNameChanged.class, data);
    }

    private int uid;
    private String localName;

    public UpdateUserLocalNameChanged(int uid, String localName) {
        this.uid = uid;
        this.localName = localName;
    }

    public UpdateUserLocalNameChanged() {

    }

    public int getUid() {
        return this.uid;
    }

    public String getLocalName() {
        return this.localName;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.localName = values.optString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        if (this.localName != null) {
            writer.writeString(2, this.localName);
        }
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
