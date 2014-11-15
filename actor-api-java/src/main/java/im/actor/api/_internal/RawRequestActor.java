package im.actor.api._internal;

import com.droidkit.actors.*;
import com.droidkit.actors.tasks.TaskActor;

import im.actor.api.ApiRequestException;
import im.actor.api.parser.Request;
import im.actor.api.parser.Response;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class RawRequestActor extends TaskActor<Response> {

    public static ActorSelection request(String path, final long id, final Request request, final ActorRef broker) {
        return new ActorSelection(Props.create(RawRequestActor.class, new ActorCreator<RawRequestActor>() {
            @Override
            public RawRequestActor create() {
                return new RawRequestActor(id, request, broker);
            }
        }), path);
    }

    private Request request;
    private ActorRef broker;
    private long id;

    public RawRequestActor(long id, Request request, ActorRef broker) {
        this.id = id;
        this.request = request;
        this.broker = broker;
    }

    @Override
    public void startTask() {
        broker.send(new ApiBrokerActor.SendRequest(id, request), self());
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof RpcError) {
            RpcError error = (RpcError) message;
            error(new ApiRequestException(
                    error.getErrorCode(),
                    error.getErrorTag(),
                    error.getErrorUserMessage(),
                    error.isCanTryAgain(),
                    error.getRelatedData()));
        } else if (message instanceof RpcResult) {
            complete(((RpcResult) message).getResult());
        }
    }

    @Override
    public void onTaskObsolete() {
        super.onTaskObsolete();
        broker.send(new ApiBrokerActor.CancelRequest(id));
    }

    /**
     * Created by ex3ndr on 04.09.14.
     */
    public static class RpcError {
        private int errorCode;
        private String errorTag;
        private String errorUserMessage;
        private boolean canTryAgain;
        private byte[] relatedData;

        public RpcError(int errorCode, String errorTag, String errorUserMessage, boolean canTryAgain, byte[] relatedData) {
            this.errorCode = errorCode;
            this.errorTag = errorTag;
            this.errorUserMessage = errorUserMessage;
            this.canTryAgain = canTryAgain;
            this.relatedData = relatedData;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorTag() {
            return errorTag;
        }

        public String getErrorUserMessage() {
            return errorUserMessage;
        }

        public boolean isCanTryAgain() {
            return canTryAgain;
        }

        public byte[] getRelatedData() {
            return relatedData;
        }
    }

    /**
     * Created by ex3ndr on 04.09.14.
     */
    public static class RpcResult {
        private Response result;

        public RpcResult(Response result) {
            this.result = result;
        }

        public Response getResult() {
            return result;
        }
    }
}
