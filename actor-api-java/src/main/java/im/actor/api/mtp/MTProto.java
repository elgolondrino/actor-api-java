package im.actor.api.mtp;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.messages.PoisonPill;
import im.actor.api.mtp._internal.EndpointProvider;
import im.actor.api.mtp._internal.MTUids;
import im.actor.api.mtp._internal.actors.ReceiverActor;
import im.actor.api.mtp._internal.actors.ManagerActor;
import im.actor.api.mtp._internal.actors.SenderActor;
import im.actor.api.mtp._internal.entity.message.RpcRequestBox;
import im.actor.api.mtp._internal.entity.message.rpc.RpcRequest;

import static com.droidkit.actors.ActorSystem.system;

public class MTProto {

    private MTProtoParams params;
    private EndpointProvider endpointProvider;
    private MTUids uids;
    private String path;
    private int connectionCount;

    private ActorRef manager;
    private ActorRef sender;
    private ActorRef receiver;

    private ActorRef stateBroker;

    public MTProto(final MTProtoParams params,
                   final String path,
                   final int desiredConnectionCount,
                   final ActorRef stateBroker) {
        if (params.getSessionId() == 0) {
            throw new RuntimeException("Session can't be zero");
        }
        if (params.getAuthId() == 0) {
            throw new RuntimeException("AuthId can't be zero");
        }
        this.path = path;
        this.params = params;
        this.endpointProvider = new EndpointProvider(params.getConfig().getEndpoints());
        this.stateBroker = stateBroker;
        this.uids = new MTUids();
        this.connectionCount = desiredConnectionCount;
        this.manager = system().actorOf(ManagerActor.manager(this));
        this.sender = system().actorOf(SenderActor.senderActor(this));
        this.receiver = system().actorOf(ReceiverActor.receiver(this));
    }

    public String getPath() {
        return path;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public MTProtoParams getParams() {
        return params;
    }

    public ActorRef getStateBroker() {
        return stateBroker;
    }

    public MTUids getUids() {
        return uids;
    }

    public EndpointProvider getEndpointProvider() {
        return endpointProvider;
    }

    public long sendRpcRequest(RpcRequest request) {
        long rid = uids.nextId();
        RpcRequestBox rpcRequestBox = new RpcRequestBox(request.toByteArray());
        sender.send(new SenderActor.SendMessage(rid, rpcRequestBox, true));
        return rid;
    }

    public void cancelRequest(long rid) {
        sender.send(new SenderActor.CancelMessage(rid));
    }

    public void notifyNetworkStateChanged() {
        manager.send(new ManagerActor.NetworkChanged());
    }

    public void close() {
        manager.send(PoisonPill.INSTANCE);
        sender.send(PoisonPill.INSTANCE);
        receiver.send(PoisonPill.INSTANCE);
    }
}
