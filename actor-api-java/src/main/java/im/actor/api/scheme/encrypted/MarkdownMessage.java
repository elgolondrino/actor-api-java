package im.actor.api.scheme.encrypted;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class MarkdownMessage extends BserObject {

    private String markdown;

    public MarkdownMessage(String markdown) {
        this.markdown = markdown;
    }

    public MarkdownMessage() {

    }

    public String getMarkdown() {
        return this.markdown;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.markdown = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.markdown == null) {
            throw new IOException();
        }
        writer.writeString(1, this.markdown);
    }

}
