package im.actor.proto.api._internal;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.tasks.AskCallback;
import com.droidkit.actors.typed.TypedActor;
import com.droidkit.actors.typed.TypedFuture;
import com.google.protobuf.Message;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ex3ndr on 10.11.14.
 */
public class TypedRequestActor extends TypedActor<TypedRequestInt> implements TypedRequestInt {

    private static final AtomicLong NEXT_RPC_ID = new AtomicLong(1);

    private ActorRef broker;

    public TypedRequestActor(ActorRef broker) {
        super(TypedRequestInt.class);
        this.broker = broker;
    }

    @Override
    public Future<Message> request(Message message) {
        final TypedFuture<Message> res = future();
        ask(RawRequestActor.request(getPath() + "/" + NEXT_RPC_ID.incrementAndGet(),
                        NEXT_RPC_ID.getAndIncrement(), message, broker),
                new AskCallback<Message>() {
                    @Override
                    public void onResult(Message result) {
                        res.doComplete(result);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        res.doError(throwable);
                    }
                });
        return res;
    }

    @Override
    public Future<Message> request(Message message, long timeout) {
        final TypedFuture<Message> res = future();
        ask(RawRequestActor.request(getPath() + "/" + NEXT_RPC_ID.incrementAndGet(),
                        NEXT_RPC_ID.getAndIncrement(), message, broker), timeout,
                new AskCallback<Message>() {
                    @Override
                    public void onResult(Message result) {
                        res.doComplete(result);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        res.doError(throwable);
                    }
                });
        return res;
    }
}
