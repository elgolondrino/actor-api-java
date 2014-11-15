package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FastThumb extends BserObject {

    private int w;
    private int h;
    private byte[] preview;

    public FastThumb(int w, int h, byte[] preview) {
        this.w = w;
        this.h = h;
        this.preview = preview;
    }

    public FastThumb() {

    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        return this.h;
    }

    public byte[] getPreview() {
        return this.preview;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.w = values.getInt(1);
        this.h = values.getInt(2);
        this.preview = values.getBytes(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.w);
        writer.writeInt(2, this.h);
        if (this.preview == null) {
            throw new IOException();
        }
        writer.writeBytes(4, this.preview);
    }

}
