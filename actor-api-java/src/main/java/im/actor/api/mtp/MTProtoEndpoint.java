package im.actor.api.mtp;

/**
 * Created by ex3ndr on 07.09.14.
 */
public class MTProtoEndpoint {
    private EndpointType endpointType;
    private String host;
    private int port;

    public MTProtoEndpoint(EndpointType endpointType, String host, int port) {
        this.endpointType = endpointType;
        this.port = port;
        this.host = host;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static enum EndpointType {
        PLAIN_TCP, TLS_TCP
    }
}
