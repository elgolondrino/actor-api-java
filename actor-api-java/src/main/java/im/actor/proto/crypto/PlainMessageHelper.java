package im.actor.proto.crypto;

import com.google.protobuf.ByteString;
import im.actor.proto.api.ActorPlainScheme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import static im.actor.proto.util.StreamingUtils.writeInt;
import static im.actor.proto.util.StreamingUtils.writeProtoBytes;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class PlainMessageHelper {

    private static final int TYPE_PLAIN_MESSAGE = 1;

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_FILE = 2;

    public static byte[] createTextMessage(long randomId, String message) {
        final ActorPlainScheme.TextMessage.Builder textMessageBuilder = ActorPlainScheme.TextMessage.newBuilder();
        textMessageBuilder.setText(message);
        textMessageBuilder.setExtType(0);
        ActorPlainScheme.TextMessage textMessage = textMessageBuilder.build();
        return createMessage(randomId, TYPE_TEXT, textMessage.toByteArray());
    }

    private static byte[] createMessage(long randomId, int type, byte[] message) {
        // Decrypted Message
        ActorPlainScheme.PlainMessage.Builder decryptedMessageBuilder = ActorPlainScheme.PlainMessage.newBuilder();
        decryptedMessageBuilder.setGuid(randomId);
        decryptedMessageBuilder.setMessageType(type);
        decryptedMessageBuilder.setBody(ByteString.copyFrom(message));
        byte[] decryptedMessage = decryptedMessageBuilder.build().toByteArray();

        // Decrypted Data
        ActorPlainScheme.PlainPackage.Builder decryptedDataBuilder = ActorPlainScheme.PlainPackage.newBuilder();
        decryptedDataBuilder.setMessageType(TYPE_PLAIN_MESSAGE);
        decryptedDataBuilder.setBody(ByteString.copyFrom(decryptedMessage));
        try {
            CRC32 crc32 = new CRC32();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            writeInt(TYPE_PLAIN_MESSAGE, stream);
            writeProtoBytes(message, stream);
            crc32.update(stream.toByteArray());
            decryptedDataBuilder.setCrc32(crc32.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            decryptedDataBuilder.setCrc32(0);
        }

        return decryptedDataBuilder.build().toByteArray();
    }
}
