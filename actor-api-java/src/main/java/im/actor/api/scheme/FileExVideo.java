package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileExVideo extends BserObject {

    private int w;
    private int h;
    private int duration;

    public FileExVideo(int w, int h, int duration) {
        this.w = w;
        this.h = h;
        this.duration = duration;
    }

    public FileExVideo() {

    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        return this.h;
    }

    public int getDuration() {
        return this.duration;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.w = values.getInt(1);
        this.h = values.getInt(2);
        this.duration = values.getInt(3);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.w);
        writer.writeInt(2, this.h);
        writer.writeInt(3, this.duration);
    }

}
