package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Phone extends BserObject {

    private int id;
    private long accessHash;
    private long phone;
    private String phoneTitle;

    public Phone(int id, long accessHash, long phone, String phoneTitle) {
        this.id = id;
        this.accessHash = accessHash;
        this.phone = phone;
        this.phoneTitle = phoneTitle;
    }

    public Phone() {

    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public long getPhone() {
        return this.phone;
    }

    public String getPhoneTitle() {
        return this.phoneTitle;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.phone = values.getLong(3);
        this.phoneTitle = values.getString(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeLong(2, this.accessHash);
        writer.writeLong(3, this.phone);
        if (this.phoneTitle == null) {
            throw new IOException();
        }
        writer.writeString(4, this.phoneTitle);
    }

}
