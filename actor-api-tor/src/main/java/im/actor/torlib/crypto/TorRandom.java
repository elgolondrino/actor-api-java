package im.actor.torlib.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import im.actor.torlib.errors.TorException;

public class TorRandom {

    private static SecureRandom createRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new TorException(e);
        }
    }

    private final SecureRandom random;

    public TorRandom() {
        random = createRandom();
    }

    public byte[] getBytes(int n) {
        final byte[] bs = new byte[n];
        random.nextBytes(bs);
        return bs;
    }

    public long nextLong(long n) {
        long bits, val;
        do {
            bits = nextLong();
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    public synchronized int nextInt(int n) {
        return random.nextInt(n);
    }

    public synchronized int nextInt() {
        return random.nextInt() & Integer.MAX_VALUE;
    }

    public synchronized long nextLong() {
        return random.nextLong() & Long.MAX_VALUE;
    }
}
