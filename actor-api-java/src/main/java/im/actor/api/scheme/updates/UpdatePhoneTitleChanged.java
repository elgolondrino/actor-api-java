package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdatePhoneTitleChanged extends Update {

    public static final int HEADER = 0x59;
    public static UpdatePhoneTitleChanged fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdatePhoneTitleChanged.class, data);
    }

    private int phoneId;
    private String title;

    public UpdatePhoneTitleChanged(int phoneId, String title) {
        this.phoneId = phoneId;
        this.title = title;
    }

    public UpdatePhoneTitleChanged() {

    }

    public int getPhoneId() {
        return this.phoneId;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneId = values.getInt(2);
        this.title = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(2, this.phoneId);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(3, this.title);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
