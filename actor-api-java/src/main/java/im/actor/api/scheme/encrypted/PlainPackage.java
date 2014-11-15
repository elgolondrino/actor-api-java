package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class PlainPackage extends BserObject {

    private int messsageType;
    private byte[] body;
    private long crc32;

    public PlainPackage(int messsageType, byte[] body, long crc32) {
        this.messsageType = messsageType;
        this.body = body;
        this.crc32 = crc32;
    }

    public PlainPackage() {

    }

    public int getMesssageType() {
        return this.messsageType;
    }

    public byte[] getBody() {
        return this.body;
    }

    public long getCrc32() {
        return this.crc32;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.messsageType = values.getInt(1);
        this.body = values.getBytes(2);
        this.crc32 = values.getLong(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.messsageType);
        if (this.body == null) {
            throw new IOException();
        }
        writer.writeBytes(2, this.body);
        writer.writeLong(3, this.crc32);
    }

}
