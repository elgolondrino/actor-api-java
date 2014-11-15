package im.actor.api.mtp.messages;

/**
 * Created by ex3ndr on 04.09.14.
 */
public class RpcError {
    private long messageId;
    private int errorCode;
    private String errorTag;
    private String errorUserMessage;
    private boolean canTryAgain;
    private byte[] relatedData;

    public RpcError(long messageId, int errorCode, String errorTag, String errorUserMessage, boolean canTryAgain,
                    byte[] relatedData) {
        this.messageId = messageId;
        this.errorCode = errorCode;
        this.errorTag = errorTag;
        this.errorUserMessage = errorUserMessage;
        this.canTryAgain = canTryAgain;
        this.relatedData = relatedData;
    }

    public long getMessageId() {
        return messageId;
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
