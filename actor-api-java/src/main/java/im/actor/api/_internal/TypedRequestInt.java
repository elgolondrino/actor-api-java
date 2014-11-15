package im.actor.api._internal;

import com.droidkit.actors.concurrency.Future;
import im.actor.api.parser.Request;
import im.actor.api.parser.Response;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface TypedRequestInt {
    public Future<Response> request(Request message);

    public Future<Response> request(Request message, long timeout);
}
