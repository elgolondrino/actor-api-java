package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class ServiceExChangedTItle extends BserObject {

    private String title;

    public ServiceExChangedTItle(String title) {
        this.title = title;
    }

    public ServiceExChangedTItle() {

    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.title = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.title == null) {
            throw new IOException();
        }
        writer.writeString(1, this.title);
    }

}
