package im.actor.api.mtp._internal;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ex3ndr on 03.09.14.
 */
public class MTUids {
    private final AtomicLong NEXT_ID = new AtomicLong(1);

    public long nextId() {
        return NEXT_ID.getAndIncrement();
    }
}
