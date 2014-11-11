package im.actor.stress;

import com.droidkit.actors.*;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.concurrency.FutureCallback;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import im.actor.proto.api.ActorApi;
import im.actor.proto.api.ActorApiConfig;
import im.actor.proto.api.ActorApiScheme;
import im.actor.proto.crypto.Crypto;
import im.actor.proto.crypto.KeyTools;
import im.actor.proto.mtp.MTProtoEndpoint;
import im.actor.stress.tools.AppLog;
import im.actor.stress.tools.EmptyApiCallback;
import im.actor.stress.tools.MemoryApiStorage;
import im.actor.stress.tools.PerformanceLog;

import java.security.KeyPair;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class AccountActor extends Actor {

    public static ActorSelection account(final long phoneNumber, final String smsCode, final long[] destPhones,
                                         final ActorRef mainActor,
                                         final KeyPair keyPair) {
        String path = "account/" + phoneNumber + "/";
        return new ActorSelection(Props.create(AccountActor.class, new ActorCreator<AccountActor>() {
            @Override
            public AccountActor create() {
                return new AccountActor(phoneNumber, smsCode, destPhones, mainActor, keyPair);
            }
        }), path);
    }

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";
    private static final String ACCOUNT_NAME = "JUnit Test";
    private static final String DEVICE_TITLE = "JUnit";
    private static final byte[] DEVICE_HASH = Crypto.SHA256("stress.test".getBytes());
    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "54.171.79.89", 8080);


    private ActorApi actorApi;

    private boolean isAuthCompleted = false;
    private ActorApiScheme.User myUser;
    private long myKey;

    private final ActorRef mainActor;

    private final long phoneNumber;
    private final String smsCode;
    private final long[] destPhones;
    private final KeyPair keyPair;

    private long startTime;

    public AccountActor(long phoneNumber, String smsCode, long[] destPhones, ActorRef mainActor, KeyPair keyPair) {
        this.phoneNumber = phoneNumber;
        this.smsCode = smsCode;
        this.destPhones = destPhones;
        this.mainActor = mainActor;
        this.keyPair = keyPair;
    }

    @Override
    public void preStart() {
        ActorApiConfig config = new ActorApiConfig.Builder()
                .setApiCallback(new EmptyApiCallback())
                .setStorage(new MemoryApiStorage())
                .addEndpoint(ENDPOINT)
                .build();
        actorApi = new ActorApi(config);

        startTime = System.currentTimeMillis();

        ask(actorApi.rpc(ActorApiScheme.RequestAuthCode.newBuilder()
                                .setAppId(APP_ID)
                                .setApiKey(APP_KEY)
                                .setPhoneNumber(phoneNumber)
                                .build(),
                        ActorApiScheme.ResponseAuthCode.class),
                new FutureCallback<ActorApiScheme.ResponseAuthCode>() {

                    @Override
                    public void onResult(ActorApiScheme.ResponseAuthCode result) {
                        onAuthCodeRequested(result);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void onAuthCodeRequested(ActorApiScheme.ResponseAuthCode authCode) {
        // Create key
        byte[] publicKey = KeyTools.encodeRsaPublicKey(keyPair.getPublic());

        ask(actorApi.rpc(ActorApiScheme.RequestSignUp.newBuilder()
                                .setAppId(APP_ID)
                                .setAppKey(APP_KEY)
                                .setPhoneNumber(phoneNumber)
                                .setSmsHash(authCode.getSmsHash())
                                .setSmsCode(smsCode)
                                .setName(ACCOUNT_NAME)
                                .setDeviceTitle(DEVICE_TITLE)
                                .setPublicKey(ByteString.copyFrom(publicKey))
                                .setDeviceHash(ByteString.copyFrom(DEVICE_HASH))
                                .build(),
                        ActorApiScheme.ResponseAuth.class),
                new FutureCallback<ActorApiScheme.ResponseAuth>() {

                    @Override
                    public void onResult(ActorApiScheme.ResponseAuth result) {
                        onAuthenticated(result);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void onAuthenticated(ActorApiScheme.ResponseAuth auth) {
        myUser = auth.getUser();
        myKey = auth.getPublicKeyHash();
        isAuthCompleted = true;

        mainActor.send(new StressActor.OnLoggedIn(phoneNumber));

        PerformanceLog.v("Authenticated", "phone", phoneNumber + "", "duration", "" + (System.currentTimeMillis() - startTime));
    }
}
