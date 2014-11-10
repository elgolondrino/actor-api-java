package im.actor.proto.api._internal;

import com.droidkit.actors.concurrency.Future;
import com.google.protobuf.Message;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface TypedRequestInt {
    public Future<Message> request(Message message);

    public Future<Message> request(Message message, long timeout);
}
