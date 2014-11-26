package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class ServiceExChangedAvatar extends BserObject {

    private Avatar avatar;

    public ServiceExChangedAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public ServiceExChangedAvatar() {

    }

    public Avatar getAvatar() {
        return this.avatar;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.avatar = values.optObj(1, Avatar.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.avatar != null) {
            writer.writeObject(1, this.avatar);
        }
    }

}
