package im.actor.api;

import im.actor.api.mtp.MTProtoEndpoint;
import im.actor.api.scheme.ApiRequests;
import im.actor.api.scheme.rpc.ResponseSendAuthCode;
import junit.framework.TestCase;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class ProxyTest extends TestCase {

    private static final MTProtoEndpoint ENDPOINT_TLS = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.TLS_TCP, "mtproto-api.actor.im", 443);
    private static final MTProtoEndpoint ENDPOINT_PLAIN = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "mtproto-api.actor.im", 8080);

    private static final MTProtoEndpoint ENDPOINT_ONION_PLAIN = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "tzzgvbbgwtgtaxg2.onion", 8080);
    private static final MTProtoEndpoint ENDPOINT_ONION_TLS = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.TLS_TCP, "tzzgvbbgwtgtaxg2.onion", 8080);

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";

    private static final long PHONE_NUMBER = 75552212121L;

    public void testProxy() throws Exception {
        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(ENDPOINT_ONION_PLAIN)
                .setLog(new SysOutLog())
                .setProxy(new ActorApiProxy("127.0.0.1", 9150))
                .build();
        ActorApi actorApi = new ActorApi(config);
        ApiRequests requests = actorApi.getRequests();

        // Request code
        ResponseSendAuthCode requestCode = requests.sendAuthCodeSync(PHONE_NUMBER, APP_ID, APP_KEY);
    }
}
