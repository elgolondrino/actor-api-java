package im.actor.torlib.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ex3ndr on 18.12.14.
 */
public final class Log {
    private Log() {
    }


    public static void d(String tag, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        System.out.println(dateFormat.format(new Date()) + " D [" + tag + "] " + message);
    }

    public static void w(String tag, String message) {
        System.out.println("W [" + tag + "] " + message);
    }
}
