package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class UploadConfig extends BserObject {

    private byte[] serverData;

    public UploadConfig(byte[] serverData) {
        this.serverData = serverData;
    }

    public UploadConfig() {

    }

    public byte[] getServerData() {
        return this.serverData;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.serverData = values.getBytes(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.serverData == null) {
            throw new IOException();
        }
        writer.writeBytes(1, this.serverData);
    }

}
