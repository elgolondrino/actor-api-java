package im.actor.proto.api;

/**
 * Created by ex3ndr on 10.11.14.
 */
public interface LogInterface {
    public void d(String tag, String message);

    public void w(String tag, String message);

    public void e(String tag, Throwable throwable);
}
