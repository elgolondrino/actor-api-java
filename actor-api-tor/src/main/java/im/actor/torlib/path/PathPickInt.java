package im.actor.torlib.path;

import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.directory.routers.Router;

/**
 * Created by ex3ndr on 18.12.14.
 */
public interface PathPickInt {
    public Future<Router> pickDirectory();
}