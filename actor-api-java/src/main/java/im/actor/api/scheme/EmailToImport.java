package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class EmailToImport extends BserObject {

    private String email;
    private String name;

    public EmailToImport(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public EmailToImport() {

    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.email = values.getString(1);
        this.name = values.optString(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.email == null) {
            throw new IOException();
        }
        writer.writeString(1, this.email);
        if (this.name != null) {
            writer.writeString(2, this.name);
        }
    }

}
