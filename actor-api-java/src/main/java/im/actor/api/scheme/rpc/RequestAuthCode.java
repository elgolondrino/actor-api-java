package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestAuthCode extends Request<ResponseAuthCode> {

    public static final int HEADER = 0x1;
    public static RequestAuthCode fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestAuthCode.class, data);
    }

    private long phoneNumber;
    private int appId;
    private String apiKey;

    public RequestAuthCode(long phoneNumber, int appId, String apiKey) {
        this.phoneNumber = phoneNumber;
        this.appId = appId;
        this.apiKey = apiKey;
    }

    public RequestAuthCode() {

    }

    public long getPhoneNumber() {
        return this.phoneNumber;
    }

    public int getAppId() {
        return this.appId;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneNumber = values.getLong(1);
        this.appId = values.getInt(2);
        this.apiKey = values.getString(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.phoneNumber);
        writer.writeInt(2, this.appId);
        if (this.apiKey == null) {
            throw new IOException();
        }
        writer.writeString(3, this.apiKey);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
