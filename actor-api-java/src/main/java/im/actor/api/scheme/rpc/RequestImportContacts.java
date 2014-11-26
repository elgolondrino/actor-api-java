package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestImportContacts extends Request<ResponseImportContacts> {

    public static final int HEADER = 0x7;
    public static RequestImportContacts fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestImportContacts.class, data);
    }

    private List<PhoneToImport> phones;
    private List<EmailToImport> emails;

    public RequestImportContacts(List<PhoneToImport> phones, List<EmailToImport> emails) {
        this.phones = phones;
        this.emails = emails;
    }

    public RequestImportContacts() {

    }

    public List<PhoneToImport> getPhones() {
        return this.phones;
    }

    public List<EmailToImport> getEmails() {
        return this.emails;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phones = values.getRepeatedObj(1, PhoneToImport.class);
        this.emails = values.getRepeatedObj(2, EmailToImport.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.phones);
        writer.writeRepeatedObj(2, this.emails);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
