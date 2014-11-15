package im.actor.api;

import im.actor.api.scheme.encrypted.PlainMessage;
import im.actor.api.scheme.encrypted.PlainPackage;
import im.actor.api.scheme.encrypted.TextMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import static im.actor.api.util.StreamingUtils.writeInt;
import static im.actor.api.util.StreamingUtils.writeProtoBytes;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class EncryptedMessageHelper {

    private static final int TYPE_PLAIN_MESSAGE = 1;

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_FILE = 2;

    public static byte[] createTextMessage(long randomId, String message) {
        return createMessage(randomId, TYPE_TEXT, new TextMessage(message, 0, null).toByteArray());
    }

    private static byte[] createMessage(long randomId, int type, byte[] message) {
        // Decrypted Message
        byte[] decryptedMessage = new PlainMessage(randomId, type, message).toByteArray();

        // Decrypted Data
        long crc32Val = 0;
        try {
            CRC32 crc32 = new CRC32();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            writeInt(TYPE_PLAIN_MESSAGE, stream);
            writeProtoBytes(message, stream);
            crc32.update(stream.toByteArray());
            crc32Val = crc32.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PlainPackage(TYPE_PLAIN_MESSAGE, decryptedMessage, crc32Val).toByteArray();
    }
}
