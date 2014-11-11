package im.actor.proto.mtp._internal.actors;

import com.droidkit.actors.*;
import im.actor.proto.api.LogInterface;
import im.actor.proto.mtp.MTProto;
import im.actor.proto.mtp._internal.MTUids;
import im.actor.proto.mtp._internal.entity.ProtoMessage;
import im.actor.proto.mtp._internal.entity.ProtoSerializer;
import im.actor.proto.mtp._internal.entity.ProtoStruct;
import im.actor.proto.mtp._internal.entity.message.*;
import im.actor.proto.mtp._internal.entity.message.rpc.RpcError;
import im.actor.proto.mtp._internal.entity.message.rpc.RpcOk;
import im.actor.proto.mtp._internal.entity.message.rpc.Update;
import im.actor.proto.mtp.messages.AuthIdInvalidated;
import im.actor.proto.mtp.messages.Confirmed;
import im.actor.proto.mtp.messages.NewSessionCreated;
import im.actor.proto.mtp.messages.RpcMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class ReceiverActor extends Actor {

    private static final String TAG = "ProtoReceiver";

    public static ActorSelection receiver(final MTProto proto) {
        return new ActorSelection(Props.create(ReceiverActor.class, new ActorCreator<ReceiverActor>() {
            @Override
            public ReceiverActor create() {
                return new ReceiverActor(proto);
            }
        }), proto.getPath() + "/receiver");
    }

    private static final int MAX_RECEIVED_BUFFER = 1000;

    private final LogInterface LOG;
    private final boolean DEBUG;

    private ActorRef sender;
    private ActorRef stateBroker;
    private MTProto proto;
    private MTUids uids;
    private ArrayList<Long> receivedMessages = new ArrayList<Long>();

    public ReceiverActor(MTProto proto) {
        this.proto = proto;
        this.stateBroker = proto.getStateBroker();
        this.uids = proto.getUids();
        this.LOG = proto.getParams().getConfig().getLogInterface();
        this.DEBUG = proto.getParams().getConfig().isDebugProto();
    }

    @Override
    public void preStart() {
        sender = system().actorOf(SenderActor.senderActor(proto));
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof ProtoMessage) {
            onReceive((ProtoMessage) message);
        }
    }

    private void onReceive(ProtoMessage message) {
        boolean disableConfirm = false;
        try {
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Received message #" + message.messageId);
            }

            if (receivedMessages.contains(message.messageId)) {
                if (LOG != null) {
                    LOG.w(TAG, "Already received message #" + message.messageId + ": ignoring");
                }
                return;
            }

            if (receivedMessages.size() >= MAX_RECEIVED_BUFFER) {
                receivedMessages.remove(0);
                receivedMessages.add(message.messageId);
            }

            ProtoStruct obj;
            try {
                obj = ProtoSerializer.readMessagePayload(message.payload);
            } catch (IOException e) {
                if (LOG != null) {
                    LOG.w(TAG, "Unable to parse message: ignoring");
                }
                e.printStackTrace();
                return;
            }

            if (LOG != null && DEBUG) {
                LOG.d(TAG, "Loaded " + obj);
            }

            if (obj instanceof Pong) {
                // TODO: Implement pong
            } else if (obj instanceof Ping) {
                Ping ping = ((Ping) obj);
                sender.send(new SenderActor.SendMessage(uids.nextId(), new Pong(ping.getRandomId()), false));
            } else if (obj instanceof Drop) {
                Drop drop = (Drop) obj;

                if (drop.message != null) {
                    final String dropMessage = drop.message.toLowerCase();
                    if (dropMessage.contains("unknown") && dropMessage.contains("auth")) {
                        stateBroker.send(new AuthIdInvalidated());
                    }
                    if (LOG != null) {
                        LOG.w(TAG, "DROP:" + drop.getMessage());
                    }
                }
            } else if (obj instanceof NewSession) {
                sender.send(new SenderActor.NewSession());
                stateBroker.send(new NewSessionCreated());
            } else if (obj instanceof Container) {
                Container container = (Container) obj;
                for (ProtoMessage m : container.getMessages()) {
                    self().send(m, sender());
                }
            } else if (obj instanceof RpcResponseBox) {
                RpcResponseBox responseBox = (RpcResponseBox) obj;

                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "Response #" + responseBox.getMessageId());
                }

                sender.send(new SenderActor.RpcResponseReceived(responseBox.getMessageId()));

                try {
                    ProtoStruct payload = ProtoSerializer.readRpcResponsePayload(responseBox.getPayload());
                    if (LOG != null && DEBUG) {
                        LOG.d(TAG, "Loaded " + payload + " from RpcResponseBox");
                    }
                    if (payload instanceof RpcOk) {
                        RpcOk rpcOk = (RpcOk) payload;
                        stateBroker.send(new RpcMessage(responseBox.getMessageId(), rpcOk.responseType, rpcOk.payload));
                    } else if (payload instanceof RpcError) {
                        RpcError rpcError = (RpcError) payload;

                        stateBroker.send(new im.actor.proto.mtp.messages
                                .RpcError(responseBox.getMessageId(), rpcError.errorCode, rpcError.errorTag,
                                rpcError.userMessage, rpcError.canTryAgain, rpcError.relatedData));
                    } else {
                        if (LOG != null) {
                            LOG.w(TAG, "Unsupported RpcResponse type");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (LOG != null) {
                        LOG.w(TAG, "Unable to load data from UpdateBox");
                    }
                }
            } else if (obj instanceof MessageAck) {
                MessageAck ack = (MessageAck) obj;

                if (LOG != null && DEBUG) {
                    LOG.d(TAG, "Ack " + Arrays.toString(ack.messagesIds));
                }

                for (long ackMsgId : ack.messagesIds) {
                    sender.send(new SenderActor.ForgetMessage(ackMsgId));
                    stateBroker.send(new Confirmed(message.messageId));
                }
            } else if (obj instanceof UpdateBox) {
                UpdateBox box = (UpdateBox) obj;
                try {
                    Update update = ProtoSerializer.readUpdate(box.getPayload());
                    stateBroker.send(new im.actor.proto.mtp.messages.Update(update.updateType, update.body));
                } catch (IOException e) {
                    e.printStackTrace();
                    if (LOG != null) {
                        LOG.w(TAG, "Unable to load data from UpdateBox");
                    }
                }
            } else if (obj instanceof UnsentResponse) {
                UnsentResponse unsent = (UnsentResponse) obj;
                if (!receivedMessages.contains(unsent.getResponseMessageId())) {
                    disableConfirm = true;
                    sender.send(new SenderActor.SendMessage(uids.nextId(),
                            new RequestResend(unsent.getMessageId()), false));
                }
            } else if (obj instanceof UnsentMessage) {
                UnsentMessage unsent = (UnsentMessage) obj;
                if (!receivedMessages.contains(unsent.getMessageId())) {
                    disableConfirm = true;
                    sender.send(new SenderActor.SendMessage(uids.nextId(),
                            new RequestResend(unsent.getMessageId()), false));
                }
            } else {
                if (LOG != null) {
                    LOG.w(TAG, "Unsupported package " + obj.getClass().getCanonicalName());
                }
            }
        } finally

        {
            if (!disableConfirm) {
                sender.send(new SenderActor.ConfirmMessage(message.messageId));
            }
        }
    }
}
