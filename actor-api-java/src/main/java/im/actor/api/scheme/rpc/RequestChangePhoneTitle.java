package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestChangePhoneTitle extends Request<ResponseSeq> {

    public static final int HEADER = 0x7c;
    public static RequestChangePhoneTitle fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestChangePhoneTitle.class, data);
    }

    private int phoneId;
    private String title;

    public RequestChangePhoneTitle(int phoneId, String title) {
        this.phoneId = phoneId;
        this.title = title;
    }

    public RequestChangePhoneTitle() {

    }

    public int getPhoneId() {
        return this.phoneId;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneId = values.getInt(1);
        this.title = values.getString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.phoneId);
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
