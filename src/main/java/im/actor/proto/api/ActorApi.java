package im.actor.proto.api;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSelection;
import com.google.protobuf.Message;

import im.actor.proto.api._internal.ApiBrokerActor;
import im.actor.proto.api._internal.RequestActor;

import java.util.concurrent.atomic.AtomicLong;

import static com.droidkit.actors.ActorSystem.system;

public class ActorApi {

    private final AtomicLong rpcCallIndex = new AtomicLong(1);

    private ActorRef broker;

    public ActorApi(ActorApiConfig reactiveConfig) {
        this.broker = system().actorOf(ApiBrokerActor.broker(reactiveConfig));
    }

    public void notifyNetworkChanged() {
        broker.send(new ApiBrokerActor.NetworkChanged());
    }

    public ActorSelection request(Message message) {
        return RequestActor.request(rpcCallIndex.incrementAndGet(), message, broker);
    }
}