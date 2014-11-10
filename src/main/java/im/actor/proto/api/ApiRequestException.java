package im.actor.proto.api;

/**
 * Created by ex3ndr on 30.08.14.
 */
public class ApiRequestException extends Exception {
    private int errorCode;
    private String errorTag;
    private String errorUserMessage;
    private boolean canTryAgain;
    private byte[] relatedData;

    public ApiRequestException(int errorCode, String errorTag, String errorUserMessage, boolean canTryAgain,
                               byte[] relatedData) {
        super(errorUserMessage);
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
