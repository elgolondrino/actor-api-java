package im.actor.stress;

import com.droidkit.actors.*;
import com.droidkit.actors.concurrency.FutureCallback;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import im.actor.proto.api.ActorApi;
import im.actor.proto.api.ActorApiConfig;
import im.actor.proto.api.ActorApiScheme;
import im.actor.proto.api.ApiRequestException;
import im.actor.proto.crypto.Crypto;
import im.actor.proto.crypto.KeyTools;
import im.actor.proto.crypto.PlainMessageHelper;
import im.actor.proto.crypto.RsaEncryptCipher;
import im.actor.proto.mtp.MTProtoEndpoint;
import im.actor.stress.tools.AppLog;
import im.actor.stress.tools.EmptyApiCallback;
import im.actor.stress.tools.MemoryApiStorage;
import im.actor.stress.tools.StashLog;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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
    private static final String ACCOUNT_NAME = "Stress Test";
    private static final String DEVICE_TITLE = "Stress Server";
    private static final byte[] DEVICE_HASH = Crypto.SHA256("stress.test".getBytes());
    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "54.171.79.89", 8080);


    private ActorApi actorApi;

    private ActorApiScheme.User myUser;
    private UserHolder myHolder;
    private long myKey;

    private final ActorRef mainActor;

    private final long phoneNumber;
    private final String smsCode;
    private final long[] destPhones;
    private final KeyPair keyPair;

    private int requestingUsers;

    private long startTime;

    private HashMap<Integer, UserHolder> users = new HashMap<Integer, UserHolder>();

    private Random rnd = new Random();

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
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof PerformAuth) {
            startAuth();
        } else if (message instanceof PerformUserLoad) {
            loadUsers();
        } else if (message instanceof PerformSend) {
            sendMessage();
        }
    }

    private void startAuth() {

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
                        AppLog.w("Received sms request error (" + phoneNumber + ") " + throwable.getMessage());
                        StashLog.w("Sms request error", "message", throwable.getMessage(), "phone", phoneNumber + "");
                        throwable.printStackTrace();
                        startAuth();
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
                        AppLog.w("Received sms code error (" + phoneNumber + ") " + throwable.getMessage());
                        StashLog.w("Sms Code error", "message", throwable.getMessage(), "phone", phoneNumber + "");
                        throwable.printStackTrace();
                        startAuth();
                    }
                });
    }

    private void onAuthenticated(ActorApiScheme.ResponseAuth auth) {
        myUser = auth.getUser();
        myKey = auth.getPublicKeyHash();
        myHolder = new UserHolder(myUser);

        mainActor.send(new StressActor.OnLoggedIn(phoneNumber));

        StashLog.v("Authenticated", "phone", phoneNumber + "", "duration", "" + (System.currentTimeMillis() - startTime));
    }

    private void loadUsers() {
        startTime = System.currentTimeMillis();
        requestingUsers = destPhones.length;
        for (int i = 0; i < destPhones.length; i++) {
            final int finalI = i;
            final long start = System.currentTimeMillis();
            ask(actorApi.rpc(ActorApiScheme.RequestFindContacts.newBuilder()
                            .setRequest("+" + destPhones[finalI])
                            .build(), ActorApiScheme.ResponseFindContacts.class),
                    new FutureCallback<ActorApiScheme.ResponseFindContacts>() {
                        @Override
                        public void onResult(ActorApiScheme.ResponseFindContacts result) {

                            StashLog.v("userSearch", "phone", phoneNumber + "", "search", "+" + destPhones[finalI], "count", result.getUsersCount() + "",
                                    "duration", (System.currentTimeMillis() - start) + "");

                            if (result.getUsersCount() == 1) {
                                users.put(result.getUsers(0).getId(), new UserHolder(result.getUsers(0)));
                            }

                            requestingUsers--;
                            if (requestingUsers == 0) {
                                onUsersLoaded();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            StashLog.w("userSearchError", "phone", phoneNumber + "", "search", "+" + destPhones[finalI],
                                    "error", throwable.getMessage(),
                                    "duration", (System.currentTimeMillis() - start) + "");

                            requestingUsers--;
                            if (requestingUsers == 0) {
                                onUsersLoaded();
                            }
                        }
                    });
        }
    }

    private void onUsersLoaded() {
        mainActor.send(new StressActor.OnUsersLoaded(phoneNumber));

        StashLog.v("Users loaded", "phone", phoneNumber + "", "duration", "" + (System.currentTimeMillis() - startTime));
    }

    private void sendMessage() {
        Integer[] uids = users.keySet().toArray(new Integer[0]);
        int uid = uids[rnd.nextInt(uids.length)];
        UserHolder destUser = users.get(uid);
        performSend(destUser);
    }

    private void performSend(final UserHolder holder) {
        boolean needKeys = false;
        ActorApiScheme.RequestPublicKeys.Builder builder = ActorApiScheme.RequestPublicKeys.newBuilder();
        for (long k : holder.keyHashes) {
            if (holder.fullKeys.containsKey(k)) {
                continue;
            }
            needKeys = true;
            builder.addKeys(ActorApiScheme.PublicKeyRequest.newBuilder()
                    .setUid(holder.uid)
                    .setAccessHash(holder.accessHash)
                    .setKeyHash(k)
                    .build());
        }

        for (long k : myHolder.keyHashes) {
            if (k == myKey) {
                continue;
            }
            if (myHolder.fullKeys.containsKey(k)) {
                continue;
            }
            needKeys = true;
            builder.addKeys(ActorApiScheme.PublicKeyRequest.newBuilder()
                    .setUid(myUser.getId())
                    .setAccessHash(myUser.getAccessHash())
                    .setKeyHash(k)
                    .build());
        }

        if (needKeys) {
            final long requestStart = System.currentTimeMillis();
            ask(actorApi.rpc(builder.build(), ActorApiScheme.ResponsePublicKeys.class),
                    new FutureCallback<ActorApiScheme.ResponsePublicKeys>() {
                        @Override
                        public void onResult(ActorApiScheme.ResponsePublicKeys result) {
                            StashLog.v("KeyRequested", "phone", phoneNumber + "", "count", "" + result.getKeysCount(),
                                    "duration", "" + (System.currentTimeMillis() - requestStart));
                            for (ActorApiScheme.PublicKey key : result.getKeysList()) {
                                if (key.getUid() == myUser.getId()) {
                                    myHolder.fullKeys.put(key.getKeyHash(), key.getKey().toByteArray());
                                } else {
                                    users.get(key.getUid()).fullKeys.put(key.getKeyHash(), key.getKey().toByteArray());
                                }
                            }

                            performSend(holder);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                            StashLog.w("KeyRequestError", "message", throwable.getMessage(), "phone", phoneNumber + "",
                                    "duration", "" + (System.currentTimeMillis() - requestStart));
                            performSend(holder);
                        }
                    });
            return;
        }


        long rid = rnd.nextLong();
        byte[] textMessage = PlainMessageHelper.createTextMessage(rid, "jUnit test");

        RsaEncryptCipher encryptCipher = new RsaEncryptCipher();

        for (long k : holder.keyHashes) {
            if (!holder.fullKeys.containsKey(k)) {
                continue;
            }
            encryptCipher.addDestination(holder.uid, k, holder.fullKeys.get(k));
        }

        for (long k : myHolder.keyHashes) {
            if (!myHolder.fullKeys.containsKey(k)) {
                continue;
            }
            encryptCipher.addDestination(holder.uid, k, holder.fullKeys.get(k));
        }

        RsaEncryptCipher.EncryptedMessage encryptedMessage = encryptCipher.encrypt(textMessage);

        // Sending message

        ActorApiScheme.EncryptedRSAMessage.Builder encryptedBuilder = ActorApiScheme.EncryptedRSAMessage.newBuilder();
//        encryptedBuilder.setEncryptedMessage(ByteString.copyFrom(textMessage));
//        for (long k : holder.keyHashes) {
//            if (!holder.fullKeys.containsKey(k)) {
//                continue;
//            }
//            encryptedBuilder.addKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
//                    .setKeyHash(k)
//                    .setAesEncryptedKey(ByteString.copyFrom(holder.fullKeys.get(k))));
//        }
//
//        for (long k : myHolder.keyHashes) {
//            if (!myHolder.fullKeys.containsKey(k)) {
//                continue;
//            }
//            encryptedBuilder.addOwnKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
//                    .setKeyHash(k)
//                    .setAesEncryptedKey(ByteString.copyFrom(myHolder.fullKeys.get(k))));
//        }


        encryptedBuilder.setEncryptedMessage(ByteString.copyFrom(encryptedMessage.getEncryptedMessage()));
        if (encryptedMessage.getResult().containsKey(myUser.getId())) {
            for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(myUser.getId())) {
                encryptedBuilder.addOwnKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
                        .setKeyHash(part.getKeyHash())
                        .setAesEncryptedKey(ByteString.copyFrom(part.getEncrypted())));
            }
        }

        for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(holder.uid)) {
            encryptedBuilder.addKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
                    .setKeyHash(part.getKeyHash())
                    .setAesEncryptedKey(ByteString.copyFrom(part.getEncrypted())));
        }

        final long requestStart = System.currentTimeMillis();
        ask(actorApi.rpc(ActorApiScheme.RequestSendMessage.newBuilder()
                .setUid(holder.uid)
                .setAccessHash(holder.accessHash)
                .setRandomId(rid)
                .setMessage(encryptedBuilder.build())
                .build(), ActorApiScheme.ResponseSeq.class), new FutureCallback<ActorApiScheme.ResponseSeq>() {

            @Override
            public void onResult(ActorApiScheme.ResponseSeq result) {
                StashLog.v("MessageSent", "phone", phoneNumber + "",
                        "duration", "" + (System.currentTimeMillis() - requestStart));
                mainActor.send(new StressActor.OnMessageSent(phoneNumber));
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                StashLog.w("MessageSendError", "message", throwable.getMessage(), "phone", phoneNumber + "",
                        "duration", "" + (System.currentTimeMillis() - requestStart));

                if (throwable instanceof ApiRequestException) {
                    ApiRequestException requestException = (ApiRequestException) throwable;
                    if (requestException.getErrorTag().equals("WRONG_KEYS")) {
                        try {
                            ActorApiScheme.WrongReceiversErrorData wrongKeys = ActorApiScheme.WrongReceiversErrorData.parseFrom(requestException.getRelatedData());
                            for (ActorApiScheme.UserKey key : wrongKeys.getNewKeysList()) {
                                if (key.getUid() == myUser.getId()) {
                                    myHolder.keyHashes.add(key.getKeyHash());
                                } else {
                                    users.get(key.getUid()).keyHashes.add(key.getKeyHash());
                                }
                            }

                            for (ActorApiScheme.UserKey key : wrongKeys.getInvalidKeysList()) {
                                if (key.getUid() == myUser.getId()) {
                                    myHolder.keyHashes.remove(key.getKeyHash());
                                } else {
                                    users.get(key.getUid()).keyHashes.remove(key.getKeyHash());
                                }
                            }

                            for (ActorApiScheme.UserKey key : wrongKeys.getRemovedKeysList()) {
                                if (key.getUid() == myUser.getId()) {
                                    myHolder.keyHashes.remove(key.getKeyHash());
                                } else {
                                    users.get(key.getUid()).keyHashes.remove(key.getKeyHash());
                                }
                            }
                            performSend(holder);
                            return;
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }

                performSend(holder);
            }
        });
    }

    public static class PerformAuth {

    }

    public static class PerformSend {

    }

    public static class PerformUserLoad {

    }

    class UserHolder {
        private int uid;
        private long accessHash;
        private HashSet<Long> keyHashes;
        private HashMap<Long, byte[]> fullKeys;
        private HashMap<Long, byte[]> encryptedMessageKeys;

        public UserHolder(ActorApiScheme.User user) {
            this.uid = user.getId();
            this.accessHash = user.getAccessHash();
            this.keyHashes = new HashSet<Long>(user.getKeyHashesList());
            this.fullKeys = new HashMap<Long, byte[]>();
            this.encryptedMessageKeys = new HashMap<Long, byte[]>();
        }
    }
}
