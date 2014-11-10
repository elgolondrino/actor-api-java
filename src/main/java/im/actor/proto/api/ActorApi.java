package im.actor.proto.api;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import com.droidkit.actors.tasks.AskTimeoutException;
import com.droidkit.actors.typed.TypedCreator;
import com.google.protobuf.Message;

import im.actor.proto.api._internal.ApiBrokerActor;
import im.actor.proto.api._internal.TypedRequestActor;
import im.actor.proto.api._internal.TypedRequestInt;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static com.droidkit.actors.ActorSystem.system;

public class ActorApi {

    private ActorRef broker;
    private TypedRequestInt requestInt;

    public ActorApi(ActorApiConfig reactiveConfig) {
        this.broker = system().actorOf(ApiBrokerActor.broker("0", reactiveConfig));
        this.requestInt = TypedCreator.typed(system().actorOf(Props.create(TypedRequestActor.class, new ActorCreator<TypedRequestActor>() {
            @Override
            public TypedRequestActor create() {
                return new TypedRequestActor(broker);
            }
        }), "actor-api/0/request"), TypedRequestInt.class);
    }

    public void notifyNetworkChanged() {
        broker.send(new ApiBrokerActor.NetworkChanged());
    }

    public <T extends Message> Future<T> rpc(Message message, FutureCallback<T> callback) {
        Future<T> res = (Future<T>) requestInt.request(message);
        if (callback != null) {
            res.addListener(callback);
        }
        return res;
    }

    public <T extends Message> Future<T> rpc(Message message, long timeout, FutureCallback<T> callback) {
        Future<T> res = (Future<T>) requestInt.request(message, timeout);
        if (callback != null) {
            res.addListener(callback);
        }
        return res;
    }

    public <T extends Message> Future<T> rpc(Message message) {
        return rpc(message, null);
    }

    public <T extends Message> Future<T> rpc(Message message, long timeout) {
        return rpc(message, timeout, null);
    }

    public <T extends Message> T rpcSync(Message message, long timeout) throws TimeoutException, ApiRequestException {
        final Object[] res = new Object[2];
        synchronized (res) {
            rpc(message, timeout, new FutureCallback<Message>() {
                @Override
                public void onResult(Message result) {
                    synchronized (res) {
                        res[0] = true;
                        res[1] = result;
                        res.notifyAll();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    synchronized (res) {
                        res[0] = false;
                        res[1] = throwable;
                        res.notifyAll();
                    }
                }
            });
            try {
                res.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new TimeoutException();
            }
            if (Boolean.TRUE.equals(res[0])) {
                return (T) res[1];
            } else {
                if (res[1] instanceof ApiRequestException) {
                    throw (ApiRequestException) res[1];
                } else if (res[1] instanceof AskTimeoutException) {
                    throw new TimeoutException();
                } else {
                    throw new RuntimeException((Throwable) res[1]);
                }
            }
        }
    }
}