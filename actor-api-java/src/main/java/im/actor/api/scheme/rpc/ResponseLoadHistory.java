package im.actor.api.scheme.rpc;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class ResponseLoadHistory extends Response {

    public static final int HEADER = 0x77;
    public static ResponseLoadHistory fromBytes(byte[] data) throws IOException {
        return Bser.parse(ResponseLoadHistory.class, data);
    }

    private List<HistoryMessage> history;
    private List<User> users;

    public ResponseLoadHistory(List<HistoryMessage> history, List<User> users) {
        this.history = history;
        this.users = users;
    }

    public ResponseLoadHistory() {

    }

    public List<HistoryMessage> getHistory() {
        return this.history;
    }

    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.history = values.getRepeatedObj(1, HistoryMessage.class);
        this.users = values.getRepeatedObj(2, User.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeRepeatedObj(1, this.history);
        writer.writeRepeatedObj(2, this.users);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
