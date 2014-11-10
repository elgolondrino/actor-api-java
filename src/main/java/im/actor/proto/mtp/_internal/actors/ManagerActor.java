package im.actor.proto.mtp._internal.actors;

import com.droidkit.actors.*;
import com.droidkit.actors.tasks.AskCallback;
import im.actor.proto.api.LogInterface;
import im.actor.proto.mtp.MTProto;
import im.actor.proto.mtp._internal.EndpointProvider;
import im.actor.proto.mtp._internal.entity.ProtoPackage;
import im.actor.proto.mtp._internal.tcp.CreateTcpConnectionActor;
import im.actor.proto.mtp._internal.tcp.TcpConnection;
import im.actor.proto.util.ExponentialBackoff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 02.09.14.
 */
public class ManagerActor extends Actor {

    private static final String TAG = "Manager";

    public static ActorSelection manager(final MTProto proto) {
        return new ActorSelection(Props.create(ManagerActor.class, new ActorCreator<ManagerActor>() {
            @Override
            public ManagerActor create() {
                return new ManagerActor(proto);
            }
        }), proto.getPath() + "/manager");
    }

    private static final AtomicInteger NEXT_CONNECTION = new AtomicInteger(1);

    private final ArrayList<TcpConnection> connections = new ArrayList<TcpConnection>();
    private final LogInterface LOG;
    private final boolean DEBUG;
    private int desiredConnections;
    private boolean isCheckingConnections;

    private int roundRobin = 0;


    private MTProto proto;

    private ActorRef receiver;
    private ActorRef sender;

    private EndpointProvider endpointProvider;
    private ExponentialBackoff backoff;

    public ManagerActor(MTProto proto) {
        this.desiredConnections = proto.getConnectionCount();
        this.proto = proto;
        this.endpointProvider = proto.getEndpointProvider();
        this.LOG = proto.getParams().getConfig().getLogInterface();
        this.DEBUG = proto.getParams().getConfig().isDebugProto();
        this.backoff = new ExponentialBackoff();
    }

    @Override
    public void preStart() {
        receiver = system().actorOf(ReceiverActor.receiver(proto));
        sender = system().actorOf(SenderActor.senderActor(proto));
        self().send(new PerformConnectionCheck());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof TcpConnection.ConnectionDie) {
            onConnectionDie((TcpConnection.ConnectionDie) message);
        } else if (message instanceof TcpConnection.RawMessage) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received #" + ((TcpConnection.RawMessage) message).getId());
            }
            onRawMessage((TcpConnection.RawMessage) message);
        } else if (message instanceof SendMessage) {
            onSendMessage((SendMessage) message);
        } else if (message instanceof NetworkChanged) {
            if (!isCheckingConnections) {
                self().sendOnce(new PerformConnectionCheck());
            }
            backoff.reset();
        } else if (message instanceof PerformConnectionCheck) {
            if (isCheckingConnections) {
                return;
            }

            if (connections.size() < desiredConnections) {
                isCheckingConnections = true;
                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "Checking connection");
                }
                ask(new ActorSelection(CreateTcpConnectionActor.props(endpointProvider.fetchEndpoint(), proto.getParams(), self()),
                                getPath() + "/connect/" + NEXT_CONNECTION.getAndIncrement()),
                        new AskCallback<TcpConnection>() {
                            @Override
                            public void onResult(TcpConnection result) {
                                if (LOG != null && DEBUG) {
                                    LOG.d(TAG, "Connection created");
                                }
                                isCheckingConnections = false;
                                connections.add(result);
                                backoff.onSuccess();

                                self().send(new PerformConnectionCheck(), self());
                                sender.send(new SenderActor.ConnectionCreated(result.getConnectionId()), self());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                if (LOG != null) {
                                    LOG.d(TAG, "Connection creation error");
                                }
                                isCheckingConnections = false;
                                backoff.onFailure();
                                self().send(new PerformConnectionCheck(), backoff.exponentialWait(), self());
                            }
                        });
            }
        }
    }

    private void onRawMessage(TcpConnection.RawMessage rawMessage) {
        if (LOG != null && DEBUG) {
            LOG.d(TAG, "Received raw message");
        }
        final ProtoPackage protoPackage;
        try {
            protoPackage = new ProtoPackage(new ByteArrayInputStream(rawMessage.getData()));
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received raw message: " + protoPackage.getPayload().messageId);
            }
        } catch (IOException e) {
            if (LOG != null) {
                LOG.e(TAG, e);
            }
            self().send(new SenderActor.ConnectionDies(rawMessage.getContextId()));
            return;
        }

        receiver.send(protoPackage.getPayload(), self());
    }

    private void onSendMessage(SendMessage sendMessage) {
        if (LOG != null && DEBUG) {
            LOG.d(TAG, "Send message #" + sendMessage.getRid());
        }
        if (connections.size() == 0) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "No connections");
            }
            return;
        }
        for (int i = 0; i < connections.size(); i++) {
            int index = (roundRobin++) % connections.size();
            TcpConnection connection = connections.get(index);
            if (!connection.isClosed()) {
                connection.postMessage(sendMessage.getMessage());
                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "Posted to connection #" + connection.getConnectionId());
                }
                reply(new ManagerActor.MessageSent(sendMessage.rid, connection.getConnectionId()));
                return;
            }
        }
    }

    private void onConnectionDie(TcpConnection.ConnectionDie connectionDie) {
        if (LOG != null && DEBUG) {
            LOG.d(TAG, "Connection dies #" + connectionDie.getContextId());
        }
        Iterator<TcpConnection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            TcpConnection connection = iterator.next();
            if (connection.getConnectionId() == connectionDie.getContextId()) {
                iterator.remove();
            }
        }
        sender.send(new SenderActor.ConnectionDies(connectionDie.getContextId()));
        self().send(new PerformConnectionCheck());
    }

    @Override
    public void postStop() {
        for (TcpConnection connection : connections) {
            connection.close();
        }
        connections.clear();
    }

    public static class SendMessage {
        private long rid;
        private byte[] message;

        public SendMessage(long rid, byte[] message) {
            this.rid = rid;
            this.message = message;
        }

        public long getRid() {
            return rid;
        }

        public byte[] getMessage() {
            return message;
        }
    }

    public static class MessageSent {
        private long rid;
        private int connectionId;

        public MessageSent(long rid, int connectionId) {
            this.rid = rid;
            this.connectionId = connectionId;
        }

        public long getRid() {
            return rid;
        }

        public int getConnectionId() {
            return connectionId;
        }
    }

    public static class NetworkChanged {

    }

    private static class PerformConnectionCheck {

    }
}
