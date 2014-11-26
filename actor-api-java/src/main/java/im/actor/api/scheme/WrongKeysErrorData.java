package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class WrongKeysErrorData extends BserObject {

    private List<UserKey> newKeys;
    private List<UserKey> removedKeys;
    private List<UserKey> invalidKeys;

    public WrongKeysErrorData(List<UserKey> newKeys, List<UserKey> removedKeys, List<UserKey> invalidKeys) {
        this.newKeys = newKeys;
        this.removedKeys = removedKeys;
        this.invalidKeys = invalidKeys;
    }

    public WrongKeysErrorData() {

    }

    public List<UserKey> getNewKeys() {
        return this.newKeys;
    }

    public List<UserKey> getRemovedKeys() {
        return this.removedKeys;
    }

    public List<UserKey> getInvalidKeys() {
        return this.invalidKeys;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.newKeys = values.getRepeatedObj(1, UserKey.class);
        this.removedKeys = values.getRepeatedObj(2, UserKey.class);
        this.invalidKeys = values.getRepeatedObj(3, UserKey.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.newKeys);
        writer.writeRepeatedObj(2, this.removedKeys);
        writer.writeRepeatedObj(3, this.invalidKeys);
    }

}
