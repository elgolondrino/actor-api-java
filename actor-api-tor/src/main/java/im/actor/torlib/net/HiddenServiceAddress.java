package im.actor.torlib.net;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class HiddenServiceAddress extends DestinationAddress {
    private String address;

    /**
     * Hidden service destination
     *
     * @param address address without onion zone name
     * @param port    destination port
     */
    public HiddenServiceAddress(String address, int port) {
        super(port);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
