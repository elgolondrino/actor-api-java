package im.actor.torlib.net;

/**
 * Created by ex3ndr on 15.12.14.
 */
public abstract class DestinationAddress {
    private int port;

    public DestinationAddress(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
