package im.actor.api;

import im.actor.api.LogInterface;

/**
 * Created by ex3ndr on 10.11.14.
 */
public class SysOutLog implements LogInterface {
    @Override
    public synchronized void d(String tag, String message) {
        System.out.println("D:" + tag + ":" + message);
    }

    @Override
    public synchronized void w(String tag, String message) {
        System.out.println("W:" + tag + ":" + message);
    }

    @Override
    public synchronized void e(String tag, Throwable throwable) {
        throwable.printStackTrace();
    }
}
