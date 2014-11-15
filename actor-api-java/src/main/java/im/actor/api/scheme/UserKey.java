package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class UserKey extends BserObject {

    private int uid;
    private long keyHash;

    public UserKey(int uid, long keyHash) {
        this.uid = uid;
        this.keyHash = keyHash;
    }

    public UserKey() {

    }

    public int getUid() {
        return this.uid;
    }

    public long getKeyHash() {
        return this.keyHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.keyHash = values.getLong(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeLong(2, this.keyHash);
    }

}
