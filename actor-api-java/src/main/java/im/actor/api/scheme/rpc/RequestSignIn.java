package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSignIn extends Request<ResponseAuth> {

    public static final int HEADER = 0x3;
    public static RequestSignIn fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSignIn.class, data);
    }

    private long phoneNumber;
    private String smsHash;
    private String smsCode;
    private byte[] publicKey;
    private byte[] deviceHash;
    private String deviceTitle;
    private int appId;
    private String appKey;

    public RequestSignIn(long phoneNumber, String smsHash, String smsCode, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey) {
        this.phoneNumber = phoneNumber;
        this.smsHash = smsHash;
        this.smsCode = smsCode;
        this.publicKey = publicKey;
        this.deviceHash = deviceHash;
        this.deviceTitle = deviceTitle;
        this.appId = appId;
        this.appKey = appKey;
    }

    public RequestSignIn() {

    }

    public long getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getSmsHash() {
        return this.smsHash;
    }

    public String getSmsCode() {
        return this.smsCode;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public byte[] getDeviceHash() {
        return this.deviceHash;
    }

    public String getDeviceTitle() {
        return this.deviceTitle;
    }

    public int getAppId() {
        return this.appId;
    }

    public String getAppKey() {
        return this.appKey;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneNumber = values.getLong(1);
        this.smsHash = values.getString(2);
        this.smsCode = values.getString(3);
        this.publicKey = values.getBytes(4);
        this.deviceHash = values.getBytes(5);
        this.deviceTitle = values.getString(6);
        this.appId = values.getInt(7);
        this.appKey = values.getString(8);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeLong(1, this.phoneNumber);
        if (this.smsHash == null) {
            throw new IOException();
        }
        writer.writeString(2, this.smsHash);
        if (this.smsCode == null) {
            throw new IOException();
        }
        writer.writeString(3, this.smsCode);
        if (this.publicKey == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.publicKey);
        if (this.deviceHash == null) {
            throw new IOException();
        }
        writer.writeBytes(5, this.deviceHash);
        if (this.deviceTitle == null) {
            throw new IOException();
        }
        writer.writeString(6, this.deviceTitle);
        writer.writeInt(7, this.appId);
        if (this.appKey == null) {
            throw new IOException();
        }
        writer.writeString(8, this.appKey);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
