package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class MessageContent extends BserObject {

    private int type;
    private byte[] content;

    public MessageContent(int type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    public MessageContent() {

    }

    public int getType() {
        return this.type;
    }

    public byte[] getContent() {
        return this.content;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.type = values.getInt(1);
        this.content = values.getBytes(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.type);
        writer.writeBytes(2, this.content);
    }

}
