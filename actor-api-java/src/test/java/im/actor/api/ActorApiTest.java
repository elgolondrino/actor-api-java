package im.actor.api;

import im.actor.api.crypto.Crypto;
import im.actor.api.crypto.KeyTools;
import im.actor.api.crypto.RsaEncryptCipher;
import im.actor.api.mtp.MTProtoEndpoint;
import im.actor.api.scheme.*;
import im.actor.api.scheme.rpc.*;
import junit.framework.TestCase;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ActorApiTest extends TestCase {

    //private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.TLS_TCP, "mtproto-api.actor.im", 443);
    private static final MTProtoEndpoint ENDPOINT = new MTProtoEndpoint(MTProtoEndpoint.EndpointType.PLAIN_TCP, "54.171.79.89", 4080);

    private static final int APP_ID = 42;
    private static final String APP_KEY = "b815c437facc0f41157633d13221336b4d8484d9ff2289acc6bba4079e994d04";

    private static final long PHONE_NUMBER = 75552212121L;
    private static final long DEST_PHONE_NUMBER = 75552232323L;
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
        ApiRequests requests = actorApi.getRequests();

        // Request code
        ResponseAuthCode requestCode = requests.requestAuthCodeSync(PHONE_NUMBER, APP_ID, APP_KEY);

        // Create key
        KeyPair keyPair = KeyTools.generateNewRsaKey();
        byte[] publicKey = KeyTools.encodeRsaPublicKey(keyPair.getPublic());

        // Sign up
        ResponseAuth auth = requests.signUpSync(PHONE_NUMBER, requestCode.getSmsHash(),
                PHONE_CODE, ACCOUNT_NAME, publicKey, DEVICE_HASH, DEVICE_TITLE, APP_ID,
                APP_KEY, false);

        User myUser = auth.getUser();

        // Find dest user
        ResponseSearchContacts contacts = actorApi.rpcSync(new RequestSearchContacts("+" + DEST_PHONE_NUMBER));

        User destUser = contacts.getUsers().get(0);

        // Loading keys

//        List<PublicKeyRequest> keyRequests = new ArrayList<PublicKeyRequest>();
//        for (long k : destUser.getKeyHashes()) {
//            keyRequests.add(new PublicKeyRequest(destUser.getId(), destUser.getAccessHash(), k));
//        }
//
//        for (long k : myUser.getKeyHashes()) {
//            if (k == auth.getPublicKeyHash()) {
//                continue;
//            }
//            keyRequests.add(new PublicKeyRequest(myUser.getId(), myUser.getAccessHash(), k));
//        }
//
//        ResponseGetPublicKeys publicKeys = actorApi.rpcSync(new RequestGetPublicKeys(keyRequests));
//        HashMap<Integer, HashMap<Long, byte[]>> keys = new HashMap<Integer, HashMap<Long, byte[]>>();
//        for (PublicKey key : publicKeys.getKeys()) {
//            if (!keys.containsKey(key.getUid())) {
//                keys.put(key.getUid(), new HashMap<Long, byte[]>());
//            }
//            keys.get(key.getUid()).put(key.getKeyHash(), key.getKey());
//        }
//
//        // Encrypting message
//
//        long rid = new Random().nextLong();
//        byte[] textMessage = EncryptedMessageHelper.createTextMessage(rid, "jUnit test");
//
//        RsaEncryptCipher encryptCipher = new RsaEncryptCipher();
//        for (Integer uid : keys.keySet()) {
//            HashMap<Long, byte[]> userKeys = keys.get(uid);
//            for (Long key : userKeys.keySet()) {
//                byte[] rawKey = userKeys.get(key);
//                encryptCipher.addDestination(uid, key, rawKey);
//            }
//        }
//
//        RsaEncryptCipher.EncryptedMessage encryptedMessage = encryptCipher.encrypt(textMessage);
//
//        // Sending message
//
//
//        List<EncryptedAesKey> ownKeys = new ArrayList<EncryptedAesKey>();
//        List<EncryptedAesKey> destKeys = new ArrayList<EncryptedAesKey>();
//
//        if (encryptedMessage.getResult().containsKey(myUser.getId())) {
//            for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(myUser.getId())) {
//                ownKeys.add(new EncryptedAesKey(part.getKeyHash(), part.getEncrypted()));
//            }
//        }
//
//        for (RsaEncryptCipher.EncryptedPart part : encryptedMessage.getResult().get(destUser.getId())) {
//            destKeys.add(new EncryptedAesKey(part.getKeyHash(), part.getEncrypted()));
//        }
//
//        try {
//            ResponseMessageSent res = actorApi.rpcSync(new RequestSendEncryptedMessage(
//                    new OutPeer(PeerType.PRIVATE, destUser.getId(), destUser.getAccessHash()),
//                    rid, textMessage, destKeys, ownKeys));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        long rid = new Random().nextLong();
        ResponseMessageSent messageSent = actorApi.getRequests().sendMessageSync(new OutPeer(PeerType.PRIVATE, destUser.getId(),
                destUser.getAccessHash()), rid, new MessageContent(1,
                new TextMessage("jUnit test", 0, null).toByteArray()));
        messageSent.toByteArray();
    }
}