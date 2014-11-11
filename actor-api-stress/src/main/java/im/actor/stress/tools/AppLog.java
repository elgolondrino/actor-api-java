package im.actor.stress.tools;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class AppLog {
    public static synchronized void v(String log) {
        System.out.println(log);
    }
}
