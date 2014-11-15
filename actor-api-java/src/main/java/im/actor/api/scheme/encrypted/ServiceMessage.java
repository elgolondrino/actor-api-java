package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class ServiceMessage extends BserObject {

    private String text;
    private int extType;
    private byte[] extension;

    public ServiceMessage(String text, int extType, byte[] extension) {
        this.text = text;
        this.extType = extType;
        this.extension = extension;
    }

    public ServiceMessage() {

    }

    public String getText() {
        return this.text;
    }

    public int getExtType() {
        return this.extType;
    }

    public byte[] getExtension() {
        return this.extension;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.text = values.getString(1);
        this.extType = values.getInt(2);
        this.extension = values.optBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.text == null) {
            throw new IOException();
        }
        writer.writeString(1, this.text);
        writer.writeInt(2, this.extType);
        if (this.extension != null) {
            writer.writeBytes(3, this.extension);
        }
    }

}
