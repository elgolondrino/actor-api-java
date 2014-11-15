package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileExPhoto extends BserObject {

    private int w;
    private int h;

    public FileExPhoto(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public FileExPhoto() {

    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        return this.h;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.w = values.getInt(1);
        this.h = values.getInt(2);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.w);
        writer.writeInt(2, this.h);
    }

}
