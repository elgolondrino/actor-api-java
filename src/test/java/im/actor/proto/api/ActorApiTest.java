package im.actor.proto.api;

import com.google.protobuf.ByteString;
import im.actor.proto.crypto.Crypto;
import im.actor.proto.crypto.KeyTools;
import im.actor.proto.crypto.PlainMessageHelper;
import im.actor.proto.crypto.RsaEncryptCipher;
import im.actor.proto.mtp.MTProtoEndpoint;
import junit.framework.TestCase;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Random;

public class ActorApiTest extends TestCase {

    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint("mtproto-api.actor.im", 443);

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";

    private static final long PHONE_NUMBER = 75552575759L;
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

        ActorApiScheme.User myUser = auth.getUser();

        // Find dest user
        ActorApiScheme.ResponseFindContacts contacts = actorApi.rpcSync(ActorApiScheme.RequestFindContacts.newBuilder()
                .setRequest("+75552212121")
                .build());

        ActorApiScheme.User destUser = contacts.getUsers(0);

        // Loading keys

        ActorApiScheme.RequestPublicKeys.Builder builder = ActorApiScheme.RequestPublicKeys.newBuilder();
        for (long k : destUser.getKeyHashesList()) {
            builder.addKeys(ActorApiScheme.PublicKeyRequest.newBuilder()
                    .setUid(destUser.getId())
                    .setAccessHash(destUser.getAccessHash())
                    .setKeyHash(k)
                    .build());
        }

        for (long k : myUser.getKeyHashesList()) {
            if (k == auth.getPublicKeyHash()) {
                continue;
            }
            builder.addKeys(ActorApiScheme.PublicKeyRequest.newBuilder()
                    .setUid(myUser.getId())
                    .setAccessHash(myUser.getAccessHash())
                    .setKeyHash(k)
                    .build());
        }

        ActorApiScheme.ResponsePublicKeys publicKeys = actorApi.rpcSync(builder.build());
        HashMap<Integer, HashMap<Long, byte[]>> keys = new HashMap<Integer, HashMap<Long, byte[]>>();
        for (ActorApiScheme.PublicKey key : publicKeys.getKeysList()) {
            if (!keys.containsKey(key.getUid())) {
                keys.put(key.getUid(), new HashMap<Long, byte[]>());
            }
            keys.get(key.getUid()).put(key.getKeyHash(), key.getKey().toByteArray());
        }

        // Encrypting message

        long rid = new Random().nextLong();
        byte[] textMessage = PlainMessageHelper.createTextMessage(rid, "jUnit test");

        RsaEncryptCipher encryptCipher = new RsaEncryptCipher();
        for (Integer uid : keys.keySet()) {
            HashMap<Long, byte[]> userKeys = keys.get(uid);
            for (Long key : userKeys.keySet()) {
                byte[] rawKey = userKeys.get(key);
                encryptCipher.addDestination(uid, key, rawKey);
            }
        }

        RsaEncryptCipher.EncryptedMessage encryptedMessage = encryptCipher.encrypt(textMessage);

        // Sending message

        ActorApiScheme.EncryptedRSAMessage.Builder encryptedBuilder = ActorApiScheme.EncryptedRSAMessage.newBuilder();
        encryptedBuilder.setEncryptedMessage(ByteString.copyFrom(encryptedMessage.getEncryptedMessage()));

        if (encryptedMessage.getResult().containsKey(myUser.getId())) {
            for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(myUser.getId())) {
                encryptedBuilder.addOwnKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
                        .setKeyHash(part.getKeyHash())
                        .setAesEncryptedKey(ByteString.copyFrom(part.getEncrypted())));
            }
        }

        for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(destUser.getId())) {
            encryptedBuilder.addKeys(ActorApiScheme.EncryptedAESKey.newBuilder()
                    .setKeyHash(part.getKeyHash())
                    .setAesEncryptedKey(ByteString.copyFrom(part.getEncrypted())));
        }

        ActorApiScheme.ResponseSeq res = actorApi.rpcSync(ActorApiScheme.RequestSendMessage.newBuilder()
                .setUid(destUser.getId())
                .setAccessHash(destUser.getAccessHash())
                .setRandomId(rid)
                .setMessage(encryptedBuilder.build())
                .build());
    }
}