package im.actor.api.scheme.updates;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;
import im.actor.api.scheme.*;

public class UpdateConfig extends Update {

    public static final int HEADER = 0x2a;
    public static UpdateConfig fromBytes(byte[] data) throws IOException {
        return Bser.parse(UpdateConfig.class, data);
    }

    private Config config;

    public UpdateConfig(Config config) {
        this.config = config;
    }

    public UpdateConfig() {

    }

    public Config getConfig() {
        return this.config;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.config = values.getObj(1, Config.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.config == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.config);
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
