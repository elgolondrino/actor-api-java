package im.actor.tor;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.debug.TraceInterface;
import com.droidkit.actors.mailbox.Envelope;
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
    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "mtproto-api.actor.im", 8080);
    private static final MTProtoEndpoint ENDPOINT_HS = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "tzzgvbbgwtgtaxg2.onion", 8080);

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";

    private static final long PHONE_NUMBER = 75552212121L;

    public void testTor() throws Exception {

        ActorSystem.system().setTraceInterface(new TraceInterface() {
            @Override
            public void onEnvelopeDelivered(Envelope envelope) {

            }

            @Override
            public void onEnvelopeProcessed(Envelope envelope, long duration) {

            }

            @Override
            public void onDrop(ActorRef sender, Object message, Actor actor) {
                System.out.println("Message dropped: " + message + " to " + actor.self().getPath());
            }

            @Override
            public void onDeadLetter(ActorRef receiver, Object message) {
                System.out.println("DeadLetter dropped: " + message + " to " + receiver.getPath());
            }

            @Override
            public void onActorDie(ActorRef ref, Exception e) {
                System.out.println("Actor Die: " + ref.getPath() + " by " + e);
                e.printStackTrace();
            }
        });

        ActorTorHelper helper = new ActorTorHelper("/Users/ex3ndr/Develop/tor_junit/");
        helper.start(9152);


        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(ENDPOINT)
                .setLog(new SysOutLog())
                .setProxy(new ActorApiProxy("127.0.0.1", 9152))
                .build();
        ActorApi actorApi = new ActorApi(config);
        ApiRequests requests = actorApi.getRequests();

        // Request code
        ResponseSendAuthCode requestCode = requests.sendAuthCodeSync(PHONE_NUMBER, APP_ID, APP_KEY, 1000000L);

        helper.getTorClient().stop();
    }

//    public void testHiddenTor() throws Exception {
//
//        ActorTorHelper helper = new ActorTorHelper("/Users/ex3ndr/Develop/tor_junit/");
//        helper.start(9153);
//
//        ActorApiConfig config = new ActorApiConfig.Builder()
//                .setApiCallback(new EmptyApiCallback())
//                .setStorage(new MemoryApiStorage())
//                .addEndpoint(ENDPOINT_HS)
//                .setLog(new SysOutLog())
//                .setProxy(new ActorApiProxy("127.0.0.1", 9153))
//                .build();
//        ActorApi actorApi = new ActorApi(config);
//        ApiRequests requests = actorApi.getRequests();
//
//        // Request code
//        ResponseSendAuthCode requestCode = requests.sendAuthCodeSync(PHONE_NUMBER, APP_ID, APP_KEY, 1000000L);
//
//        helper.getTorClient().stop();
//    }
}
