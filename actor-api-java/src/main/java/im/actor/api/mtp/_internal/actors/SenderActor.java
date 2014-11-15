package im.actor.api.mtp._internal.actors;

import com.droidkit.actors.*;
import im.actor.api.LogInterface;
import im.actor.api.mtp.MTProto;
import im.actor.api.mtp.MTProtoParams;
import im.actor.api.mtp._internal.MTUids;
import im.actor.api.mtp._internal.entity.ProtoMessage;
import im.actor.api.mtp._internal.entity.ProtoPackage;
import im.actor.api.mtp._internal.entity.ProtoStruct;
import im.actor.api.mtp._internal.entity.message.Container;
import im.actor.api.mtp._internal.entity.message.MessageAck;
import im.actor.api.mtp._internal.entity.message.Ping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ex3ndr on 02.09.14.
 */
public class SenderActor extends Actor {

    private static final String TAG = "ProtoSender";

    public static ActorSelection senderActor(final MTProto proto) {
        return new ActorSelection(Props.create(SenderActor.class, new ActorCreator<SenderActor>() {
            @Override
            public SenderActor create() {
                return new SenderActor(proto);
            }
        }), proto.getPath() + "/sender");
    }


    private static final int ACK_THRESHOLD = 10;
    private static final int ACK_DELAY = 10 * 1000;
    private static final int MAX_WORKLOAD_SIZE = 1024;

    private final LogInterface LOG;
    private final boolean DEBUG;

    private ActorRef manager;
    private MTProtoParams params;
    private MTUids uids;
    private HashMap<Long, UnsentPackageHolder> unsentPackages;
    private HashSet<Long> confirm;
    private MTProto proto;

    public SenderActor(MTProto proto) {
        this.proto = proto;
        this.uids = proto.getUids();
        this.params = proto.getParams();
        this.unsentPackages = new HashMap<Long, UnsentPackageHolder>();
        this.confirm = new HashSet<Long>();

        this.LOG = proto.getParams().getConfig().getLogInterface();
        this.DEBUG = proto.getParams().getConfig().isDebugProto();
    }

    @Override
    public void preStart() {
        manager = system().actorOf(ManagerActor.manager(proto));
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof SendMessage) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received SendMessage #" + ((SendMessage) message).rid);
            }
            SendMessage sendMessage = (SendMessage) message;
            doSend(new ProtoMessage(sendMessage.rid, sendMessage.struct.toByteArray()));
            unsentPackages.put(sendMessage.rid, new UnsentPackageHolder(sendMessage.struct, sendMessage.rid, sendMessage.isRpc));
        } else if (message instanceof ConnectionCreated) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received ConnectionCreated #" + ((ConnectionCreated) message).connectionId);
            }
            ArrayList<ProtoMessage> toSend = new ArrayList<ProtoMessage>();
            for (UnsentPackageHolder unsentPackage : unsentPackages.values()) {
                if (unsentPackage.needResend) {
                    if (LOG != null && DEBUG) {
                        LOG.d(TAG, "ReSending #" + unsentPackage.rid);
                    }
                    toSend.add(new ProtoMessage(unsentPackage.rid, unsentPackage.message.toByteArray()));
                }
            }
            // HACK for init connection
            if (toSend.size() == 0) {
                toSend.add(new ProtoMessage(uids.nextId(), new Ping(uids.nextId()).toByteArray()));
            }
            doSend(toSend);
        } else if (message instanceof ConnectionDies) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received ConnectionDies #" + ((ConnectionDies) message).connectionId);
            }
            ArrayList<ProtoMessage> toSend = new ArrayList<ProtoMessage>();
            for (UnsentPackageHolder unsentPackage : unsentPackages.values()) {
                if (unsentPackage.sentToConnection == ((ConnectionDies) message).connectionId) {
                    unsentPackage.needResend = true;
                    unsentPackage.sentToConnection = 0;
                    toSend.add(new ProtoMessage(unsentPackage.rid, unsentPackage.message.toByteArray()));
                }
            }
            doSend(toSend);
        } else if (message instanceof ForgetMessage) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received ForgetMessage #" + ((ForgetMessage) message).rid);
            }
            UnsentPackageHolder packageHolder = unsentPackages.get(((ForgetMessage) message).rid);
            if (packageHolder != null && !packageHolder.isRpc) {
                unsentPackages.remove(((ForgetMessage) message).rid);
            }
        } else if (message instanceof RpcResponseReceived) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received RpcResponseReceived #" + ((RpcResponseReceived) message).rid);
            }
            unsentPackages.remove(((RpcResponseReceived) message).rid);
        } else if (message instanceof CancelMessage) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received CancelMessage #" + ((CancelMessage) message).rid);
            }
            unsentPackages.remove(((CancelMessage) message).rid);
        } else if (message instanceof ManagerActor.MessageSent) {
            ManagerActor.MessageSent sent = (ManagerActor.MessageSent) message;
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received MessageSent #" + sent.getRid() + " to #" + sent.getConnectionId());
            }
            if (unsentPackages.containsKey(sent.getRid())) {
                UnsentPackageHolder unsentPackage = unsentPackages.get(sent.getRid());
                unsentPackage.sentToConnection = sent.getConnectionId();
                unsentPackage.needResend = false;
                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "Message #" + sent.getRid() + " marked");
                }
            }
        } else if (message instanceof ConfirmMessage) {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Confirming message #" + ((ConfirmMessage) message).rid);
            }
            confirm.add(((ConfirmMessage) message).rid);
            if (confirm.size() >= ACK_THRESHOLD) {
                self().sendOnce(new ForceAck());
            } else if (confirm.size() == 1) {
                self().sendOnce(new ForceAck(), ACK_DELAY);
            }
        } else if (message instanceof ForceAck) {
            if (confirm.size() == 0) {
                return;
            }
            if (LOG != null && DEBUG) {
                String acks = "";
                for (Long l : confirm) {
                    if (acks.length() != 0) {
                        acks += ",";
                    }
                    acks += "#" + l;
                }
                LOG.d(TAG, "Sending acks " + acks);
            }
            MessageAck messageAck = buildAck();
            confirm.clear();
            doSend(new ProtoMessage(uids.nextId(), messageAck.toByteArray()));
        } else if (message instanceof NewSession) {
            // Resending all messages
            ArrayList<ProtoMessage> toSend = new ArrayList<ProtoMessage>();
            for (UnsentPackageHolder unsentPackage : unsentPackages.values()) {
                unsentPackage.needResend = true;
                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "ReSending #" + unsentPackage.rid);
                }
                toSend.add(new ProtoMessage(unsentPackage.rid, unsentPackage.message.toByteArray()));
            }
            doSend(toSend);
        }
    }

    private MessageAck buildAck() {
        long[] ids = new long[confirm.size()];
        Long[] ids2 = confirm.toArray(new Long[confirm.size()]);
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ids2[i];
        }
        return new MessageAck(ids);
    }

    private void doSend(List<ProtoMessage> items) {
        if (items.size() > 0) {
            if (confirm.size() > 0) {
                items.add(0, new ProtoMessage(uids.nextId(), buildAck().toByteArray()));
                confirm.clear();
            }
        }
        if (items.size() == 1) {
            doSend(items.get(0));
        } else if (items.size() > 1) {
            ArrayList<ProtoMessage> messages = new ArrayList<ProtoMessage>();
            int currentPayload = 0;
            for (int i = 0; i < items.size(); i++) {
                ProtoMessage message = items.get(i);
                currentPayload += message.getLength();
                messages.add(message);
                if (currentPayload > MAX_WORKLOAD_SIZE) {
                    Container container = new Container(messages.toArray(new ProtoMessage[messages.size()]));
                    performSend(new ProtoMessage(uids.nextId(), container.toByteArray()));

                    messages.clear();
                    currentPayload = 0;
                }
            }
            if (messages.size() > 0) {
                Container container = new Container(messages.toArray(new ProtoMessage[messages.size()]));
                performSend(new ProtoMessage(uids.nextId(), container.toByteArray()));
            }
        }
    }

    private void doSend(ProtoMessage message) {
        if (confirm.size() > 0) {
            ArrayList<ProtoMessage> mtpMessages = new ArrayList<ProtoMessage>();
            mtpMessages.add(message);
            doSend(mtpMessages);
        } else {
            performSend(message);
        }
    }

    private void performSend(ProtoMessage message) {
        try {
            final ProtoPackage protoPackage = new ProtoPackage(params.getAuthId(), params.getSessionId(),
                    message);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(protoPackage.getLength());
            protoPackage.writeObject(outputStream);
            byte[] data = outputStream.toByteArray();
            manager.send(new ManagerActor.SendMessage(message.messageId, data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SendMessage {
        private long rid;
        private ProtoStruct struct;
        private boolean isRpc;

        public SendMessage(long rid, ProtoStruct struct, boolean isRpc) {
            this.isRpc = isRpc;
            this.rid = rid;
            this.struct = struct;
        }
    }

    public static class ForgetMessage {
        private long rid;

        public ForgetMessage(long rid) {
            this.rid = rid;
        }
    }

    public static class CancelMessage {
        private long rid;

        public CancelMessage(long rid) {
            this.rid = rid;
        }
    }

    public static class RpcResponseReceived {
        private long rid;

        public RpcResponseReceived(long rid) {
            this.rid = rid;
        }
    }

    public static class ConfirmMessage {
        private long rid;

        public ConfirmMessage(long rid) {
            this.rid = rid;
        }
    }

    public static class ConnectionCreated {
        private int connectionId;

        public ConnectionCreated(int connectionId) {
            this.connectionId = connectionId;
        }
    }

    public static class ConnectionDies {
        private int connectionId;

        public ConnectionDies(int connectionId) {
            this.connectionId = connectionId;
        }
    }

    public static class NewSession {

    }

    public static class ForceAck {

    }

    private class UnsentPackageHolder {
        private final long rid;
        private final ProtoStruct message;
        private int sentToConnection;
        private boolean needResend;
        private boolean isRpc;

        private UnsentPackageHolder(ProtoStruct message, long rid, boolean isRpc) {
            this.message = message;
            this.rid = rid;
            this.needResend = true;
            this.isRpc = isRpc;
        }
    }
}
