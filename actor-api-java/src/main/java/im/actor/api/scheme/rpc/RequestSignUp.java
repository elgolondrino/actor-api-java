package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class RequestSignUp extends Request<ResponseAuth> {

    public static final int HEADER = 0x4;
    public static RequestSignUp fromBytes(byte[] data) throws IOException {
        return Bser.parse(RequestSignUp.class, data);
    }

    private long phoneNumber;
    private String smsHash;
    private String smsCode;
    private String name;
    private byte[] publicKey;
    private byte[] deviceHash;
    private String deviceTitle;
    private int appId;
    private String appKey;
    private boolean isSilent;

    public RequestSignUp(long phoneNumber, String smsHash, String smsCode, String name, byte[] publicKey, byte[] deviceHash, String deviceTitle, int appId, String appKey, boolean isSilent) {
        this.phoneNumber = phoneNumber;
        this.smsHash = smsHash;
        this.smsCode = smsCode;
        this.name = name;
        this.publicKey = publicKey;
        this.deviceHash = deviceHash;
        this.deviceTitle = deviceTitle;
        this.appId = appId;
        this.appKey = appKey;
        this.isSilent = isSilent;
    }

    public RequestSignUp() {

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

    public String getName() {
        return this.name;
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

    public boolean isSilent() {
        return this.isSilent;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.phoneNumber = values.getLong(1);
        this.smsHash = values.getString(2);
        this.smsCode = values.getString(3);
        this.name = values.getString(4);
        this.publicKey = values.getBytes(6);
        this.deviceHash = values.getBytes(7);
        this.deviceTitle = values.getString(8);
        this.appId = values.getInt(9);
        this.appKey = values.getString(10);
        this.isSilent = values.getBool(11);
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
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(4, this.name);
        if (this.publicKey == null) {
            throw new IOException();
        }
        writer.writeBytes(6, this.publicKey);
        if (this.deviceHash == null) {
            throw new IOException();
        }
        writer.writeBytes(7, this.deviceHash);
        if (this.deviceTitle == null) {
            throw new IOException();
        }
        writer.writeString(8, this.deviceTitle);
        writer.writeInt(9, this.appId);
        if (this.appKey == null) {
            throw new IOException();
        }
        writer.writeString(10, this.appKey);
        writer.writeBool(11, this.isSilent);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
