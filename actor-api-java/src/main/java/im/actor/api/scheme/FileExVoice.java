package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileExVoice extends BserObject {

    private int duration;

    public FileExVoice(int duration) {
        this.duration = duration;
    }

    public FileExVoice() {

    }

    public int getDuration() {
        return this.duration;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.duration = values.getInt(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.duration);
    }

}
