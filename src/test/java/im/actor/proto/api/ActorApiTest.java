package im.actor.proto.api;

import im.actor.proto.mtp.MTProtoEndpoint;
import junit.framework.TestCase;

public class ActorApiTest extends TestCase {

    public void testAuth() throws Exception {
        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(new MTProtoEndpoint("mtproto-api.actor.im", 443))
                .setLog(new SysOutLog())
                .build();
        ActorApi actorApi = new ActorApi(config);
        ActorApiScheme.ResponseAuthCode message = actorApi.rpcSync(ActorApiScheme.RequestAuthCode.newBuilder()
                .setApiKey("???")
                .setAppId(1)
                .setPhoneNumber(75552575757L)
                .build(), 5000);
    }
}