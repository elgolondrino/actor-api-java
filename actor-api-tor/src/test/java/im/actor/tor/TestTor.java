package im.actor.tor;

import im.actor.api.*;
import im.actor.api.mtp.MTProtoEndpoint;
import im.actor.api.scheme.ApiRequests;
import im.actor.api.scheme.rpc.ResponseSendAuthCode;
import junit.framework.TestCase;

/**
 * Created by ex3ndr on 08.12.14.
 */
public class TestTor extends TestCase {
    // private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.TLS_TCP, "mtproto-api.actor.im", 443);
    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "54.171.79.89", 8080);

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";

    private static final long PHONE_NUMBER = 75552212121L;

    public void testTor() throws Exception {
        ActorTorHelper helper = new ActorTorHelper();

        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(ENDPOINT)
                .setLog(new SysOutLog())
                .setProxy(helper.getProxy())
                .build();
        ActorApi actorApi = new ActorApi(config);
        ApiRequests requests = actorApi.getRequests();

        // Request code
        ResponseSendAuthCode requestCode = requests.sendAuthCodeSync(PHONE_NUMBER, APP_ID, APP_KEY);
    }
}
