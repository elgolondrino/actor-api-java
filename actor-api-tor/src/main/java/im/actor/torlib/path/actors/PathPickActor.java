package im.actor.torlib.path.actors;

import com.droidkit.actors.typed.TypedActor;
import im.actor.torlib.directory.routers.Router;

/**
 * Created by ex3ndr on 18.12.14.
 */
public class PathPickActor extends TypedActor<PathPickInt> implements PathPickInt {

    public PathPickActor() {
        super(PathPickInt.class);
    }

    @Override
    public Router pickDirectory() {
        return null;
    }
}
