package im.actor.stress.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class PerformanceLog {
    private static Logger logger = LoggerFactory.getLogger(PerformanceLog.class);

    public static void v(String message, String... obj) {
        MDC.clear();
        MDC.put("unit", "stress_unit");
        for (int i = 0; i < obj.length - 1; i += 2) {
            MDC.put(obj[i], obj[i + 1]);
        }
        logger.debug(message);
    }
}
