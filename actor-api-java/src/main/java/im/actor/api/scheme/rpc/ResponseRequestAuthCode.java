package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseRequestAuthCode extends Response {

    public static final int HEADER = 0x2;
    public static ResponseRequestAuthCode fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseRequestAuthCode.class, data);
    }

    private String smsHash;
    private boolean isRegistered;

    public ResponseRequestAuthCode(String smsHash, boolean isRegistered) {
        this.smsHash = smsHash;
        this.isRegistered = isRegistered;
    }

    public ResponseRequestAuthCode() {

    }

    public String getSmsHash() {
        return this.smsHash;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.smsHash = values.getString(1);
        this.isRegistered = values.getBool(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.smsHash == null) {
            throw new IOException();
        }
        writer.writeString(1, this.smsHash);
        writer.writeBool(2, this.isRegistered);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
