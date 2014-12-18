package im.actor.torlib.utils;

import java.nio.charset.Charset;

/**
 * The <code>Tor</code> class is a collection of static methods for instantiating
 * various subsystem modules.
 */
public class Tor {
    private final static String implementation = "AcTOR";
    private final static String version = "0.0.1";

    private final static Charset defaultCharset = createDefaultCharset();

    private static Charset createDefaultCharset() {
        return Charset.forName("ISO-8859-1");
    }

    public static Charset getDefaultCharset() {
        return defaultCharset;
    }

    public static String getImplementation() {
        return implementation;
    }

    public static String getFullVersion() {
        return getVersion();
    }

    /**
     * Return a string describing the version of this software.
     *
     * @return A string representation of the software version.
     */

    public static String getVersion() {
        return version;
    }
}
