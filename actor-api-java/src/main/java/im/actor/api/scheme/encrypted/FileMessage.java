package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class FileMessage extends BserObject {

    private String name;
    private String mimeType;
    private PlainFileLocation fileLocation;
    private FastThumb fastThumb;
    private int extType;
    private byte[] extension;

    public FileMessage(String name, String mimeType, PlainFileLocation fileLocation, FastThumb fastThumb, int extType, byte[] extension) {
        this.name = name;
        this.mimeType = mimeType;
        this.fileLocation = fileLocation;
        this.fastThumb = fastThumb;
        this.extType = extType;
        this.extension = extension;
    }

    public FileMessage() {

    }

    public String getName() {
        return this.name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public PlainFileLocation getFileLocation() {
        return this.fileLocation;
    }

    public FastThumb getFastThumb() {
        return this.fastThumb;
    }

    public int getExtType() {
        return this.extType;
    }

    public byte[] getExtension() {
        return this.extension;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.name = values.getString(1);
        this.mimeType = values.getString(2);
        this.fileLocation = values.getObj(3, PlainFileLocation.class);
        this.fastThumb = values.optObj(4, FastThumb.class);
        this.extType = values.getInt(5);
        this.extension = values.optBytes(6);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.name == null) {
            throw new IOException();
        }
        writer.writeString(1, this.name);
        if (this.mimeType == null) {
            throw new IOException();
        }
        writer.writeString(2, this.mimeType);
        if (this.fileLocation == null) {
            throw new IOException();
        }
        writer.writeObject(3, this.fileLocation);
        if (this.fastThumb != null) {
            writer.writeObject(4, this.fastThumb);
        }
        writer.writeInt(5, this.extType);
        if (this.extension != null) {
            writer.writeBytes(6, this.extension);
        }
    }

}
