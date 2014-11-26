package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestGetContacts extends Request<ResponseGetContacts> {

    public static final int HEADER = 0x57;
    public static RequestGetContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestGetContacts.class, data);
    }

    private String contactsHash;

    public RequestGetContacts(String contactsHash) {
        this.contactsHash = contactsHash;
    }

    public RequestGetContacts() {

    }

    public String getContactsHash() {
        return this.contactsHash;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.contactsHash = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.contactsHash == null) {
            throw new IOException();
        }
        writer.writeString(1, this.contactsHash);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
