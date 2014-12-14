package im.actor.torlib.circuits.actors;

import im.actor.utils.IPv4Address;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class IpDestinationAddress extends DestinationAddress {
    private IPv4Address address;

    public IpDestinationAddress(IPv4Address address, int port) {
        super(port);
        this.address = address;
    }

    public IPv4Address getAddress() {
        return address;
    }
}
