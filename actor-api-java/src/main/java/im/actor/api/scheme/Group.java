package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Group extends BserObject {

    private int id;
    private long accessHash;
    private String title;
    private Avatar avatar;
    private boolean isMember;
    private int adminUid;
    private List<Integer> members;
    private long createDate;

    public Group(int id, long accessHash, String title, Avatar avatar, boolean isMember, int adminUid, List<Integer> members, long createDate) {
        this.id = id;
        this.accessHash = accessHash;
        this.title = title;
        this.avatar = avatar;
        this.isMember = isMember;
        this.adminUid = adminUid;
        this.members = members;
        this.createDate = createDate;
    }

    public Group() {

    }

    public int getId() {
        return this.id;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public String getTitle() {
        return this.title;
    }

    public Avatar getAvatar() {
        return this.avatar;
    }

    public boolean isMember() {
        return this.isMember;
    }

    public int getAdminUid() {
        return this.adminUid;
    }

    public List<Integer> getMembers() {
        return this.members;
    }

    public long getCreateDate() {
        return this.createDate;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.accessHash = values.getLong(2);
        this.title = values.getString(3);
        this.avatar = values.optObj(4, Avatar.class);
        this.isMember = values.getBool(6);
        this.adminUid = values.getInt(8);
        this.members = values.getRepeatedInt(9);
        this.createDate = values.getLong(10);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeLong(2, this.accessHash);
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(3, this.title);
        if (this.avatar != null) {
            writer.writeObject(4, this.avatar);
        }
        writer.writeBool(6, this.isMember);
        writer.writeInt(8, this.adminUid);
        writer.writeRepeatedInt(9, this.members);
        writer.writeLong(10, this.createDate);
    }

}
