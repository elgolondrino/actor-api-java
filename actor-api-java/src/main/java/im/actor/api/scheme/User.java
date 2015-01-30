package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class User extends BserObject {

    private int id;
    private long accessHash;
    private String name;
    private String localName;
    private Sex sex;
    private List<Long> keyHashes;
    private long phone;
    private Avatar avatar;
    private List<Integer> phones;
    private List<Integer> emails;
    private UserState userState;

    public User(int id, long accessHash, String name, String localName, Sex sex, List<Long> keyHashes, long phone, Avatar avatar, List<Integer> phones, List<Integer> emails, UserState userState) {
        this.id = id;
        this.accessHash = accessHash;
        this.name = name;
        this.localName = localName;
        this.sex = sex;
        this.keyHashes = keyHashes;
        this.phone = phone;
        this.avatar = avatar;
        this.phones = phones;
        this.emails = emails;
        this.userState = userState;
    }

    public User() {

    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public String getName() {
        return this.name;
    }

    public String getLocalName() {
        return this.localName;
    }

    public Sex getSex() {
        return this.sex;
    }

    public List<Long> getKeyHashes() {
        return this.keyHashes;
    }

    public long getPhone() {
        return this.phone;
    }

    public Avatar getAvatar() {
        return this.avatar;
    }

    public List<Integer> getPhones() {
        return this.phones;
    }

    public List<Integer> getEmails() {
        return this.emails;
    }

    public UserState getUserState() {
        return this.userState;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.name = values.getString(3);
        this.localName = values.optString(4);
        int val_sex = values.getInt(5, 0);
        if (val_sex != 0) {
            this.sex = Sex.parse(val_sex);
        }
        this.keyHashes = values.getRepeatedLong(6);
        this.phone = values.getLong(7);
        this.avatar = values.optObj(8, Avatar.class);
        this.phones = values.getRepeatedInt(9);
        this.emails = values.getRepeatedInt(10);
        this.userState = UserState.parse(values.getInt(11));
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeLong(2, this.accessHash);
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(3, this.name);
        if (this.localName != null) {
            writer.writeString(4, this.localName);
        }
        if (this.sex != null) {
            writer.writeInt(5, this.sex.getValue());
        }
        writer.writeRepeatedLong(6, this.keyHashes);
        writer.writeLong(7, this.phone);
        if (this.avatar != null) {
            writer.writeObject(8, this.avatar);
        }
        writer.writeRepeatedInt(9, this.phones);
        writer.writeRepeatedInt(10, this.emails);
        if (this.userState == null) {
            throw new IOException();
        }
        writer.writeInt(11, this.userState.getValue());
    }

}
