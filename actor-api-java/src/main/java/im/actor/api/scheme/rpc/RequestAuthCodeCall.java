package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestAuthCodeCall extends Request<ResponseVoid> {

    public static final int HEADER = 0x5a;
    public static RequestAuthCodeCall fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestAuthCodeCall.class, data);
    }

    private long phoneNumber;
    private String smsHash;
    private int appId;
    private String apiKey;

    public RequestAuthCodeCall(long phoneNumber, String smsHash, int appId, String apiKey) {
        this.phoneNumber = phoneNumber;
        this.smsHash = smsHash;
        this.appId = appId;
        this.apiKey = apiKey;
    }

    public RequestAuthCodeCall() {

    }

    public long getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getSmsHash() {
        return this.smsHash;
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
        this.smsHash = values.getString(2);
        this.appId = values.getInt(3);
        this.apiKey = values.getString(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.phoneNumber);
        if (this.smsHash == null) {
            throw new IOException();
        }
        writer.writeString(2, this.smsHash);
        writer.writeInt(3, this.appId);
        if (this.apiKey == null) {
            throw new IOException();
        }
        writer.writeString(4, this.apiKey);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
