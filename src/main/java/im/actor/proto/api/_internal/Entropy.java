package im.actor.proto.api._internal;

import java.security.SecureRandom;

/**
 * Created by ex3ndr on 07.09.14.
 */
public class Entropy {
    private static final SecureRandom random = new SecureRandom();

    public static long randomId() {
        synchronized (random) {
            return random.nextLong();
        }
    }
}
