package im.actor.stress.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static net.logstash.logback.marker.Markers.*;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class PerformanceLog {
    private static Logger logger = LoggerFactory.getLogger(PerformanceLog.class);

    public static synchronized void v(String message, String... obj) {

        HashMap<String, String> myMap = new HashMap<String, String>();
        for (int i = 0; i < obj.length - 1; i += 2) {
            myMap.put(obj[i], obj[i + 1]);
        }

        logger.debug(appendEntries(myMap), message);
    }
}
