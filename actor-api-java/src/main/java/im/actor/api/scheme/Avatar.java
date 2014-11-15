package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class Avatar extends BserObject {

    private AvatarImage smallImage;
    private AvatarImage largeImage;
    private AvatarImage fullImage;

    public Avatar(AvatarImage smallImage, AvatarImage largeImage, AvatarImage fullImage) {
        this.smallImage = smallImage;
        this.largeImage = largeImage;
        this.fullImage = fullImage;
    }

    public Avatar() {

    }

    public AvatarImage getSmallImage() {
        return this.smallImage;
    }

    public AvatarImage getLargeImage() {
        return this.largeImage;
    }

    public AvatarImage getFullImage() {
        return this.fullImage;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.smallImage = values.optObj(1, AvatarImage.class);
        this.largeImage = values.optObj(2, AvatarImage.class);
        this.fullImage = values.optObj(3, AvatarImage.class);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.smallImage != null) {
            writer.writeObject(1, this.smallImage);
        }
        if (this.largeImage != null) {
            writer.writeObject(2, this.largeImage);
        }
        if (this.fullImage != null) {
            writer.writeObject(3, this.fullImage);
        }
    }

}
