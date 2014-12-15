package im.actor.torlib.circuits.build;

import com.droidkit.actors.concurrency.Future;
import im.actor.torlib.circuits.TorStream;

/**
 * Created by ex3ndr on 15.12.14.
 */
public interface DirectoryCircuitsInt {
    Future<TorStream> openDirectoryStream();
}
