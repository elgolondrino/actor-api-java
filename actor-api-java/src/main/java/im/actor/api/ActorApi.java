package im.actor.api;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import com.droidkit.actors.tasks.AskTimeoutException;
import com.droidkit.actors.typed.TypedCreator;

import im.actor.api._internal.ApiBrokerActor;
import im.actor.api._internal.TypedRequestActor;
import im.actor.api._internal.TypedRequestInt;
import im.actor.api.parser.Request;
import im.actor.api.parser.Response;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.droidkit.actors.ActorSystem.system;

public class ActorApi {

    private static AtomicInteger NEXT_ID = new AtomicInteger(0);

    private ActorRef broker;
    private TypedRequestInt requestInt;
    private int id;

    public ActorApi(ActorApiConfig reactiveConfig) {
        this.id = NEXT_ID.getAndIncrement();
        this.broker = system().actorOf(ApiBrokerActor.broker("" + id, reactiveConfig));
        this.requestInt = TypedCreator.typed(system().actorOf(Props.create(TypedRequestActor.class, new ActorCreator<TypedRequestActor>() {
            @Override
            public TypedRequestActor create() {
                return new TypedRequestActor(broker);
            }
        }), "/actor-api/" + id + "/rpc"), TypedRequestInt.class);
    }

    public <T extends Response> Future<T> rpc(Request<T> message, FutureCallback<T> callback) {
        Future<T> res = (Future<T>) requestInt.request(message);
        if (callback != null) {
            res.addListener(callback);
        }
        return res;
    }

    public <T extends Response> Future<T> rpc(Request<T> message, long timeout, FutureCallback<T> callback) {
        Future<T> res = (Future<T>) requestInt.request(message, timeout);
        if (callback != null) {
            res.addListener(callback);
        }
        return res;
    }


    public <T extends Response> Future<T> rpc(Request<T> message) {
        return rpc(message, null);
    }

    public <T extends Response> Future<T> rpc(Request<T> message, long timeout) {
        return rpc(message, timeout, null);
    }


    public <T extends Response> T rpcSync(Request<T> message) throws TimeoutException, ApiRequestException {
        return rpcSync(message, 5000);
    }

    public <T extends Response> T rpcSync(Request<T> message, long timeout) throws TimeoutException, ApiRequestException {
        final Object[] res = new Object[2];
        synchronized (res) {
            rpc(message, timeout, new FutureCallback<T>() {
                @Override
                public void onResult(T result) {
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


    public void notifyNetworkChanged() {
        broker.send(new ApiBrokerActor.NetworkChanged());
    }

    public void dispose() {
        broker.send(new ApiBrokerActor.Destroy());
    }
}