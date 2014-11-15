package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class PlainMessage extends BserObject {

    private long guid;
    private int messageTyoe;
    private byte[] body;

    public PlainMessage(long guid, int messageTyoe, byte[] body) {
        this.guid = guid;
        this.messageTyoe = messageTyoe;
        this.body = body;
    }

    public PlainMessage() {

    }

    public long getGuid() {
        return this.guid;
    }

    public int getMessageTyoe() {
        return this.messageTyoe;
    }

    public byte[] getBody() {
        return this.body;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.guid = values.getLong(1);
        this.messageTyoe = values.getInt(2);
        this.body = values.getBytes(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.guid);
        writer.writeInt(2, this.messageTyoe);
        if (this.body == null) {
            throw new IOException();
        }
        writer.writeBytes(3, this.body);
    }

}
