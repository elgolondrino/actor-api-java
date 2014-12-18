package im.actor.torlib.circuits.build;

import com.droidkit.actors.typed.TypedFuture;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.utils.IPv4Address;

public class ExitCircuitStreamRequest implements ExitTarget {

    private final TypedFuture<TorStream> res;

    private final boolean isAddress;
    private final IPv4Address address;
    private final String hostname;
    private final int port;

    private boolean isReserved;

    public ExitCircuitStreamRequest(IPv4Address address, int port, TypedFuture<TorStream> res) {
        this(true, "", address, port, res);
    }

    public ExitCircuitStreamRequest(String hostname, int port, TypedFuture<TorStream> res) {
        this(false, hostname, null, port, res);
    }

    private ExitCircuitStreamRequest(boolean isAddress, String hostname, IPv4Address address, int port, TypedFuture<TorStream> res) {
        this.res = res;
        this.isAddress = isAddress;
        this.hostname = hostname;
        this.address = address;
        this.port = port;
    }

    public boolean isAddressTarget() {
        return isAddress;
    }

    public IPv4Address getAddress() {
        return address;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public void complete(TorStream stream) {
        res.doComplete(stream);
    }

    public void error(Exception e) {
        res.doError(e);
    }

    public boolean reserveRequest() {
        if (isReserved) return false;
        isReserved = true;
        return true;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public String toString() {
        if (isAddress)
            return address + ":" + port;
        else
            return hostname + ":" + port;
    }
}
