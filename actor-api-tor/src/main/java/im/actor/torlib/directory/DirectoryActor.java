package im.actor.torlib.directory;

import com.droidkit.actors.typed.TypedActor;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class DirectoryActor extends TypedActor<DirectoryInt> implements DirectoryInt {
    public DirectoryActor() {
        super(DirectoryInt.class);
    }
}
