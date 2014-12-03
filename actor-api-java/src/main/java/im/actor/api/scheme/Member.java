package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Member extends BserObject {

    private int uid;
    private int inviterUid;
    private long date;

    public Member(int uid, int inviterUid, long date) {
        this.uid = uid;
        this.inviterUid = inviterUid;
        this.date = date;
    }

    public Member() {

    }

    public int getUid() {
        return this.uid;
    }

    public int getInviterUid() {
        return this.inviterUid;
    }

    public long getDate() {
        return this.date;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.inviterUid = values.getInt(2);
        this.date = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeInt(2, this.inviterUid);
        writer.writeLong(3, this.date);
    }

}
