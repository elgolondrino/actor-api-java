package im.actor.api.scheme.encrypted;
import java.io.IOException;

public enum EncryptionType {

    NONE(0),
    AES(1),
    AES_THEN_MAC(2);

    private int value;

    EncryptionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EncryptionType parse(int value) throws IOException {
        switch(value) {
            case 0: return EncryptionType.NONE;
            case 1: return EncryptionType.AES;
            case 2: return EncryptionType.AES_THEN_MAC;
        }
        throw new IOException();
    }
}
