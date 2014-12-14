package im.actor.torlib.utils;

import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import im.actor.torlib.TorConfig;
import im.actor.torlib.config.TorConfigProxy;

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

    /**
     * Determine if running on Android by inspecting java.runtime.name property.
     *
     * @return True if running on Android.
     */
    public static boolean isAndroidRuntime() {
        final String runtime = System.getProperty("java.runtime.name");
        return runtime != null && runtime.equals("Android Runtime");
    }

    /**
     * Create and return a new <code>TorConfig</code> instance.
     *
     * @return A new <code>TorConfig</code> instance.
     * @see im.actor.torlib.TorConfig
     */
    static public TorConfig createConfig() {
        final TorConfig config = (TorConfig) Proxy.newProxyInstance(TorConfigProxy.class.getClassLoader(), new Class[]{TorConfig.class}, new TorConfigProxy());
        if (isAndroidRuntime()) {
            config.setHandshakeV2Enabled(false);
        }
        return config;
    }
}
