package im.actor.proto.crypto;

import im.actor.proto.util.StreamingUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ex3ndr on 10.11.14.
 */
public class RsaEncryptCipher {

    private ArrayList<Destination> destinations = new ArrayList<Destination>();

    private byte[] aesKey;
    private byte[] aesIv;

    public RsaEncryptCipher(byte[] aesKey, byte[] aesIv) {
        this.aesKey = aesKey;
        this.aesIv = aesIv;
    }

    public RsaEncryptCipher() {
        aesKey = Crypto.generateSeed(32);
        aesIv = Crypto.generateSeed(16);
    }

    public void addDestination(int uid, long keyHash, byte[] key) {
        destinations.add(new Destination(uid, keyHash, key));
    }

    public EncryptedMessage encrypt(byte[] message) {
        byte[] srcData = Utils.align(Utils.concat(StreamingUtils.intToBytes(message.length), message), 16);

        byte[] key = Utils.concat(aesKey, aesIv);

        Cipher aes = Crypto.createAESCipher();

        IvParameterSpec ivSpec = new IvParameterSpec(aesIv);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");

        final byte[] encryptedMessage;
        try {
            aes.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encryptedMessage = aes.doFinal(srcData, 0, srcData.length);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        HashMap<Integer, ArrayList<EncryptedPart>> res = new HashMap<Integer, ArrayList<EncryptedPart>>();
        Cipher cipher = Crypto.createRSACipher();

        for (Destination destination : destinations) {
            EncryptedPart encryptedPart;

            try {
                PublicKey pk = KeyTools.decodeRsaPublicKey(destination.getKey());
                cipher.init(Cipher.ENCRYPT_MODE, pk);
                byte[] encryptedKey = cipher.doFinal(key);
                encryptedPart = new EncryptedPart(destination.getKeyHash(), true, encryptedKey);
            } catch (Throwable t) {
                encryptedPart = new EncryptedPart(destination.getKeyHash(), false, new byte[0]);
            }

            if (!res.containsKey(destination.getUid())) {
                res.put(destination.getUid(), new ArrayList<EncryptedPart>());
            }

            res.get(destination.getUid()).add(encryptedPart);
        }

        return new EncryptedMessage(encryptedMessage, res);
    }

    private class Destination {
        private int uid;
        private long keyHash;
        private byte[] key;

        public Destination(int uid, long keyHash, byte[] key) {
            this.uid = uid;
            this.keyHash = keyHash;
            this.key = key;
        }

        public int getUid() {
            return uid;
        }

        public long getKeyHash() {
            return keyHash;
        }

        public byte[] getKey() {
            return key;
        }
    }

    public static class EncryptedMessage {
        private byte[] encryptedMessage;

        private HashMap<Integer, ArrayList<EncryptedPart>> result = new HashMap<Integer, ArrayList<EncryptedPart>>();

        public EncryptedMessage(byte[] encryptedMessage, HashMap<Integer, ArrayList<EncryptedPart>> result) {
            this.encryptedMessage = encryptedMessage;
            this.result = result;
        }

        public byte[] getEncryptedMessage() {
            return encryptedMessage;
        }

        public HashMap<Integer, ArrayList<EncryptedPart>> getResult() {
            return result;
        }
    }

    public static class EncryptedPart {
        public long keyHash;
        public boolean isSuccessful;
        public byte[] encrypted;

        public EncryptedPart(long keyHash, boolean isSuccessful, byte[] encrypted) {
            this.keyHash = keyHash;
            this.isSuccessful = isSuccessful;
            this.encrypted = encrypted;
        }

        public long getKeyHash() {
            return keyHash;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public byte[] getEncrypted() {
            return encrypted;
        }
    }
}
