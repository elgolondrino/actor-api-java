package im.actor.api.mtp;

import im.actor.api.ActorApiConfig;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class MTProtoParams {
    private final long authId;
    private final long sessionId;
    private final ActorApiConfig config;

    public MTProtoParams(long authId, long sessionId, ActorApiConfig config) {
        this.authId = authId;
        this.sessionId = sessionId;
        this.config = config;
    }

    public long getAuthId() {
        return authId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public ActorApiConfig getConfig() {
        return config;
    }
}
