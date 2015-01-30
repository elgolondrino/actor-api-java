package im.actor.api.scheme;
import java.io.IOException;

public enum UserState {

    REGISTERED(1),
    EMAIL(2),
    DELETED(3);

    private int value;

    UserState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserState parse(int value) throws IOException {
        switch(value) {
            case 1: return UserState.REGISTERED;
            case 2: return UserState.EMAIL;
            case 3: return UserState.DELETED;
        }
        throw new IOException();
    }
}
