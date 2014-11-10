package im.actor.proto.mtp;

/**
 * Created by ex3ndr on 07.09.14.
 */
public class MTProtoEndpoint {
    private String host;
    private int port;

    public MTProtoEndpoint(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
