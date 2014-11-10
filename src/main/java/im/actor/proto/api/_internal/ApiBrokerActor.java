package im.actor.proto.api._internal;

import com.droidkit.actors.*;
import com.droidkit.actors.tasks.AskCallback;
import com.google.protobuf.Message;

import im.actor.proto.api.ActorApiCallback;
import im.actor.proto.api.ActorApiConfig;
import im.actor.proto.api.ActorApiScheme;
import im.actor.proto.api.LogInterface;
import im.actor.proto.mtp.MTProto;
import im.actor.proto.mtp.MTProtoParams;
import im.actor.proto.mtp.messages.*;
import im.actor.proto.mtp._internal.entity.message.rpc.RpcRequest;
import im.actor.proto.api._internal.parsers.RpcParser;
import im.actor.proto.api._internal.parsers.UpdatesParser;
import im.actor.proto.api._internal.parsers.SchemeParser;
import im.actor.proto.mtp.messages.AuthIdInvalidated;
import im.actor.proto.mtp.messages.NewSessionCreated;
import im.actor.proto.util.ExponentialBackoff;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class ApiBrokerActor extends Actor {

    public static ActorSelection broker(final String id, final ActorApiConfig config) {
        return new ActorSelection(Props.create(ApiBrokerActor.class, new ActorCreator<ApiBrokerActor>() {
            @Override
            public ApiBrokerActor create() {
                return new ApiBrokerActor(id, config);
            }
        }), "actor-api/" + id + "/api_broker");
    }

    private static final String TAG = "ApiBroker";
    private static final int CONNECTIONS_COUNT = 1;

    private final LogInterface LOG;
    private final boolean DEBUG;
    private final String ID;
    private final ActorApiConfig CONFIG;

    private final SchemeParser rpcParser;
    private final SchemeParser updatesParser;

    private MTProto protocol;
    private HashMap<Long, RequestHolder> requests = new HashMap<Long, RequestHolder>();
    private HashMap<Long, Long> idMap = new HashMap<Long, Long>();
    private ActorApiCallback apiCallback;
    private ExponentialBackoff backoff;

    public ApiBrokerActor(String id, ActorApiConfig config) {
        this.LOG = config.getLogInterface();
        this.DEBUG = config.isDebugLog();
        this.ID = id;
        this.CONFIG = config;
        this.apiCallback = config.getApiCallback();
        this.rpcParser = new RpcParser();
        this.updatesParser = new UpdatesParser();
        this.backoff = new ExponentialBackoff();
    }

    @Override
    public void preStart() {
        if (CONFIG.getApiStorage().getAuthKey() != 0) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "We have auth id, creating proto");
            }
            MTProtoParams params = new MTProtoParams(CONFIG.getApiStorage().getAuthKey(), Entropy.randomId(), CONFIG);
            protocol = new MTProto(params, CONNECTIONS_COUNT, self());
        } else {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Requesting auth id");
            }
            ask(KeyBuildActor.key(CONFIG, backoff), new AskCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    self().send(new KeyCreated(result));
                }

                @Override
                public void onError(Throwable throwable) {
                    // Unexpected
                }
            });
        }
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof SendRequest) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "New request #" + ((SendRequest) message).randomId);
            }
            SendRequest req = (SendRequest) message;
            if (!rpcParser.supportMessage(req.getMessage())) {
                sender().send(new RawRequestActor.RpcError(
                        0,
                        "APP_ERROR",
                        "Unsupported API Method " + req.getMessage().getClass(),
                        false, new byte[0]));
                return;
            }
            RequestHolder holder = new RequestHolder(
                    req.getRandomId(),
                    new RpcRequest(rpcParser.getId(req.getMessage()), req.getMessage().toByteArray()),
                    sender());
            requests.put(holder.publicId, holder);
            if (protocol != null) {
                holder.protoId = protocol.sendRpcRequest(holder.message);
                if (DEBUG && LOG != null) {
                    LOG.d(TAG, "#" + ((SendRequest) message).randomId + " -> #" + holder.protoId);
                }
                idMap.put(holder.protoId, holder.publicId);
            }
        } else if (message instanceof CancelRequest) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Cancel request #" + ((CancelRequest) message).getRequestId());
            }
            RequestHolder holder = requests.remove(((CancelRequest) message).getRequestId());
            if (holder != null) {
                if (protocol != null) {
                    protocol.cancelRequest(holder.protoId);
                }
                idMap.remove(holder.protoId);
            }
        } else if (message instanceof NetworkChanged) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Network changed");
            }
            if (protocol != null) {
                protocol.notifyNetworkStateChanged();
            }
        } else if (message instanceof KeyCreated) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Key created");
            }
            onKeyCreated(((KeyCreated) message).getKey());
        } else if (message instanceof AuthIdInvalidated) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "AuthIdInvalidated");
            }
            apiCallback.onAuthIdInvalidated();
        } else if (message instanceof Confirmed) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Confirmed #" + ((Confirmed) message).getMessageId());
            }
            // TODO: Implement confirmation
        } else if (message instanceof NewSessionCreated) {
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "NewSessionCreated");
            }
            apiCallback.onNewSessionCreated();
        } else if (message instanceof im.actor.proto.mtp.messages.RpcError) {
            im.actor.proto.mtp.messages.RpcError rpcError = (im.actor.proto.mtp.messages.RpcError) message;
            if (DEBUG && LOG != null) {
                LOG.w(TAG, "RpcError #" + rpcError.getErrorCode() + " " + rpcError.getErrorTag());
            }
            if (idMap.containsKey(rpcError.getMessageId())) {
                long pubId = idMap.remove(rpcError.getMessageId());
                RequestHolder holder = requests.remove(pubId);
                if (holder != null) {
                    holder.receiver.send(
                            new RawRequestActor.RpcError(
                                    rpcError.getErrorCode(),
                                    rpcError.getErrorTag(),
                                    rpcError.getErrorUserMessage(),
                                    rpcError.isCanTryAgain(),
                                    rpcError.getRelatedData()));
                }
            }
        } else if (message instanceof RpcMessage) {
            RpcMessage rpcMessage = (RpcMessage) message;
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "RpcResponse #" + rpcMessage.getMessageId());
            }
            if (idMap.containsKey(rpcMessage.getMessageId())) {
                long pubId = idMap.get(rpcMessage.getMessageId());
                RequestHolder holder = requests.get(pubId);
                if (holder != null) {
                    try {
                        Message result = rpcParser.read(rpcMessage.getPayloadType(), rpcMessage.getPayload());
                        requests.remove(pubId);
                        idMap.remove(rpcMessage.getMessageId());
                        holder.receiver.send(new RawRequestActor.RpcResult(result));
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Just ignore message
                    }
                }
            }
        } else if (message instanceof Update) {
            Update update = (Update) message;
            if (DEBUG && LOG != null) {
                LOG.d(TAG, "Received update");
            }
            try {
                Message msg = rpcParser.read(update.getUpdateType(), update.getPayload());
                if (msg instanceof ActorApiScheme.SeqUpdate) {
                    ActorApiScheme.SeqUpdate commonUpdate = (ActorApiScheme.SeqUpdate) msg;
                    if (DEBUG && LOG != null) {
                        LOG.d(TAG, "Received update: common");
                    }
                    try {
                        Message upd = updatesParser.read(commonUpdate.getUpdateHeader(), commonUpdate.getUpdate().toByteArray());
                        apiCallback.onSeqUpdate(commonUpdate.getSeq(),
                                commonUpdate.getState().toByteArray(),
                                upd);
                    } catch (IOException e) {
                        if (LOG != null) {
                            LOG.w(TAG, "Unable to load update");
                            LOG.e(TAG, e);
                        }
                        apiCallback.onSeqTooLong();
                    }
                } else if (msg instanceof ActorApiScheme.FatSeqUpdate) {
                    ActorApiScheme.FatSeqUpdate seqUpdate = (ActorApiScheme.FatSeqUpdate) msg;
                    try {
                        Message upd = updatesParser.read(seqUpdate.getUpdateHeader(), seqUpdate.getUpdate().toByteArray());
                        apiCallback.onSeqFatUpdate(seqUpdate.getSeq(),
                                seqUpdate.getState().toByteArray(),
                                upd,
                                seqUpdate.getUsersList());
                    } catch (IOException e) {
                        if (LOG != null) {
                            LOG.w(TAG, "Unable to load update");
                            LOG.e(TAG, e);
                        }
                        apiCallback.onSeqTooLong();
                    }
                } else if (msg instanceof ActorApiScheme.SeqUpdateTooLong) {
                    if (DEBUG && LOG != null) {
                        LOG.d(TAG, "Received update: too long");
                    }
                    apiCallback.onSeqTooLong();
                } else if (msg instanceof ActorApiScheme.WeakUpdate) {
                    if (DEBUG && LOG != null) {
                        LOG.d(TAG, "Received update: weak update");
                    }
                    ActorApiScheme.WeakUpdate weakUpdate = (ActorApiScheme.WeakUpdate) msg;
                    Message weakMessage = updatesParser.read(weakUpdate.getUpdateId(), weakUpdate.getUpdate().toByteArray());
                    apiCallback.onWeakUpdate(weakUpdate.getDate(), weakMessage);
                }
            } catch (IOException e) {
                if (LOG != null) {
                    LOG.w(TAG, "Unable to parse update");
                }
                e.printStackTrace();
            }
        }
    }

    private void onKeyCreated(long key) {
        CONFIG.getApiStorage().saveAuthKey(key);
        MTProtoParams params = new MTProtoParams(key, Entropy.randomId(), CONFIG);
        protocol = new MTProto(params, CONNECTIONS_COUNT, self());
        for (RequestHolder holder : requests.values()) {
            if (holder.protoId != 0) {
                continue;
            }
            holder.protoId = protocol.sendRpcRequest(holder.message);
            if (LOG != null && DEBUG) {
                LOG.d(TAG, "#" + holder.publicId + " -> #" + holder.protoId);
            }
            idMap.put(holder.protoId, holder.publicId);
        }
    }

    public static final class SendRequest {
        private long randomId;
        private Message message;

        public SendRequest(long randomId, Message message) {
            this.randomId = randomId;
            this.message = message;
        }

        public long getRandomId() {
            return randomId;
        }

        public Message getMessage() {
            return message;
        }
    }

    public static final class CancelRequest {
        private long requestId;

        public CancelRequest(long requestId) {
            this.requestId = requestId;
        }

        public long getRequestId() {
            return requestId;
        }
    }

    public static final class NetworkChanged {

    }

    private static final class KeyCreated {

        private long key;

        public KeyCreated(long key) {
            this.key = key;
        }

        public long getKey() {
            return key;
        }
    }

    private class RequestHolder {
        private RpcRequest message;
        private ActorRef receiver;
        private long publicId;
        private long protoId;

        private RequestHolder(long publicId, RpcRequest message, ActorRef receiver) {
            this.message = message;
            this.receiver = receiver;
            this.publicId = publicId;
        }
    }
}