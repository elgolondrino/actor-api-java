package im.actor.proto.api;

import com.droidkit.actors.ActorSystem;
import com.google.protobuf.ByteString;
import im.actor.proto.crypto.Crypto;
import im.actor.proto.crypto.KeyTools;
import im.actor.proto.mtp.MTProtoEndpoint;
import junit.framework.TestCase;

import java.security.KeyPair;

import static com.droidkit.actors.ActorSystem.system;

public class ActorApiTest extends TestCase {

    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint("mtproto-api.actor.im", 443);

    private static final int APP_ID = 1;
    private static final String APP_KEY = "???";
    private static final long PHONE_NUMBER = 75552575757L;
    private static final String PHONE_CODE = "2222";

    private static final String ACCOUNT_NAME = "JUnit Test";

    private static final String DEVICE_TITLE = "JUnit";

    private static final byte[] DEVICE_HASH = Crypto.SHA256("junit.test".getBytes());

    public void testAuth() throws Exception {
        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(ENDPOINT)
                .setLog(new SysOutLog())
                .build();
        ActorApi actorApi = new ActorApi(config);

        // Request code
        ActorApiScheme.ResponseAuthCode requestCode = actorApi.rpcSync(ActorApiScheme.RequestAuthCode.newBuilder()
                .setAppId(APP_ID)
                .setApiKey(APP_KEY)
                .setPhoneNumber(PHONE_NUMBER)
                .build());

        // Create key
        KeyPair keyPair = KeyTools.generateNewRsaKey();
        byte[] publicKey = KeyTools.encodeRsaPublicKey(keyPair.getPublic());

        // Sign up
        ActorApiScheme.ResponseAuth auth = actorApi.rpcSync(ActorApiScheme.RequestSignUp.newBuilder()
                .setAppId(APP_ID)
                .setAppKey(APP_KEY)
                .setPhoneNumber(PHONE_NUMBER)
                .setSmsHash(requestCode.getSmsHash())
                .setSmsCode(PHONE_CODE)
                .setName(ACCOUNT_NAME)
                .setDeviceTitle(DEVICE_TITLE)
                .setPublicKey(ByteString.copyFrom(publicKey))
                .setDeviceHash(ByteString.copyFrom(DEVICE_HASH))
                .build());

        ActorSystem actorSystem = system();

        long keyHash = auth.getPublicKeyHash();
        ActorApiScheme.User user = auth.getUser();


    }
}