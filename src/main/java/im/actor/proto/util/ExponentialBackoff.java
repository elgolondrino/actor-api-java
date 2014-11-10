package im.actor.proto.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ExponentialBackoff {

    private static final int MIN_DELAY = 100;
    private static final int MAX_DELAY = 15000;
    private static final int MAX_FAILURE_COUNT = 50;

    private final AtomicInteger currentFailureCount = new AtomicInteger();
    private final Random random = new Random();

    public long exponentialWait() {
        long maxDelay = MIN_DELAY + ((MAX_DELAY - MIN_DELAY) / MAX_FAILURE_COUNT) * currentFailureCount.get();
        synchronized (random) {
            return (long) (random.nextFloat() * maxDelay);
        }
    }

    public void onFailure() {
        final int val = currentFailureCount.incrementAndGet();
        if (val > 50) {
            currentFailureCount.compareAndSet(val, MAX_FAILURE_COUNT);
        }
    }

    public void onSuccess() {
        reset();
    }

    public void reset() {
        currentFailureCount.set(0);
    }
}