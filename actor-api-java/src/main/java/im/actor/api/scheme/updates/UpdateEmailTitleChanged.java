package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateEmailTitleChanged extends Update {

    public static final int HEADER = 0x62;
    public static UpdateEmailTitleChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateEmailTitleChanged.class, data);
    }

    private int emailId;
    private String title;

    public UpdateEmailTitleChanged(int emailId, String title) {
        this.emailId = emailId;
        this.title = title;
    }

    public UpdateEmailTitleChanged() {

    }

    public int getEmailId() {
        return this.emailId;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.emailId = values.getInt(1);
        this.title = values.getString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.emailId);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(2, this.title);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
