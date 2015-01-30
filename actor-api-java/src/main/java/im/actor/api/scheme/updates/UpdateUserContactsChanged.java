package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateUserContactsChanged extends Update {

    public static final int HEADER = 0x56;
    public static UpdateUserContactsChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateUserContactsChanged.class, data);
    }

    private int uid;
    private List<Integer> phones;
    private List<Integer> emails;

    public UpdateUserContactsChanged(int uid, List<Integer> phones, List<Integer> emails) {
        this.uid = uid;
        this.phones = phones;
        this.emails = emails;
    }

    public UpdateUserContactsChanged() {

    }

    public int getUid() {
        return this.uid;
    }

    public List<Integer> getPhones() {
        return this.phones;
    }

    public List<Integer> getEmails() {
        return this.emails;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uid = values.getInt(1);
        this.phones = values.getRepeatedInt(2);
        this.emails = values.getRepeatedInt(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.uid);
        writer.writeRepeatedInt(2, this.phones);
        writer.writeRepeatedInt(3, this.emails);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
