package im.actor.torlib.circuits.actors;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class HostAddress extends DestinationAddress {
    private String host;

    public HostAddress(String host, int port) {
        super(port);
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
