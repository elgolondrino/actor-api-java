package im.actor.api.scheme;
import java.io.IOException;

public enum Sex {

    UNKNOWN(1),
    MALE(2),
    FEMALE(3);

    private int value;

    Sex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Sex parse(int value) throws IOException {
        switch(value) {
            case 1: return Sex.UNKNOWN;
            case 2: return Sex.MALE;
            case 3: return Sex.FEMALE;
        }
        throw new IOException();
    }
}
