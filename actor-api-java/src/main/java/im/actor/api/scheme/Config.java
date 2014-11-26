package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Config extends BserObject {

    private int maxGroupSize;

    public Config(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public Config() {

    }

    public int getMaxGroupSize() {
        return this.maxGroupSize;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.maxGroupSize = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.maxGroupSize);
    }

}
