package im.actor.torlib.path;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedCreator;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.directory.routers.Router;

/**
 * Created by ex3ndr on 18.12.14.
 */
public class PathPickActor extends TypedActor<PathPickInt> implements PathPickInt {

    public static PathPickInt get(final NewDirectory directory) {
        return TypedCreator.typed(ActorSystem.system().actorOf(
                Props.create(PathPickActor.class, new ActorCreator<PathPickActor>() {
                    @Override
                    public PathPickActor create() {
                        return new PathPickActor(directory);
                    }
                }), "/tor/dir/path/picker"), PathPickInt.class);
    }

    private NewDirectory newDirectory;

    public PathPickActor(NewDirectory newDirectory) {
        super(PathPickInt.class);
        this.newDirectory = newDirectory;
    }

    @Override
    public Future<Router> pickDirectory() {
        return null;
    }
}
