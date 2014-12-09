package im.actor.api;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class ActorApiProxy {
    private String host;
    private int port;

    public ActorApiProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
