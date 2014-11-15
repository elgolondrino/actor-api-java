package im.actor.api.mtp.messages;

/**
* Created by ex3ndr on 04.09.14.
*/
public class Update {
    private int updateType;
    private byte[] payload;

    public Update(int updateType, byte[] payload) {
        this.updateType = updateType;
        this.payload = payload;
    }

    public int getUpdateType() {
        return updateType;
    }

    public byte[] getPayload() {
        return payload;
    }
}
