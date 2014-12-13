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
    private final static Logger logger = Logger.getLogger(Tor.class.getName());

    public final static int BOOTSTRAP_STATUS_STARTING = 0;
    public final static int BOOTSTRAP_STATUS_CONN_DIR = 5;
    public final static int BOOTSTRAP_STATUS_HANDSHAKE_DIR = 10;
    public final static int BOOTSTRAP_STATUS_ONEHOP_CREATE = 15;
    public final static int BOOTSTRAP_STATUS_REQUESTING_STATUS = 20;
    public final static int BOOTSTRAP_STATUS_LOADING_STATUS = 25;
    public final static int BOOTSTRAP_STATUS_REQUESTING_KEYS = 35;
    public final static int BOOTSTRAP_STATUS_LOADING_KEYS = 40;
    public final static int BOOTSTRAP_STATUS_REQUESTING_DESCRIPTORS = 45;
    public final static int BOOTSTRAP_STATUS_LOADING_DESCRIPTORS = 50;
    public final static int BOOTSTRAP_STATUS_CONN_OR = 80;
    public final static int BOOTSTRAP_STATUS_HANDSHAKE_OR = 85;
    public final static int BOOTSTRAP_STATUS_CIRCUIT_CREATE = 90;
    public final static int BOOTSTRAP_STATUS_DONE = 100;


    private final static String implementation = "Orchid";
    private final static String version = "1.0.0";

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
            logger.warning("Android Runtime detected, disabling V2 Link protocol");
            config.setHandshakeV2Enabled(false);
        }
        return config;
    }
}
