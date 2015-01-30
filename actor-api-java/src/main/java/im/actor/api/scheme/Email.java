package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Email extends BserObject {

    private int id;
    private long accessHash;
    private String email;
    private String emailTitle;

    public Email(int id, long accessHash, String email, String emailTitle) {
        this.id = id;
        this.accessHash = accessHash;
        this.email = email;
        this.emailTitle = emailTitle;
    }

    public Email() {

    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public String getEmail() {
        return this.email;
    }

    public String getEmailTitle() {
        return this.emailTitle;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.email = values.getString(3);
        this.emailTitle = values.getString(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeLong(2, this.accessHash);
        if (this.email == null) {
            throw new IOException();
        }
        writer.writeString(3, this.email);
        if (this.emailTitle == null) {
            throw new IOException();
        }
        writer.writeString(4, this.emailTitle);
    }

}
