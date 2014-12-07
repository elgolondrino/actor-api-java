package im.actor.tor;

import com.subgraph.orchid.TorClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by ex3ndr on 08.12.14.
 */
public class ActorTorHelper {
    private TorClient torClient;

    public ActorTorHelper() {
        this.torClient = new TorClient();
        this.torClient.start();
        this.torClient.enableSocksListener(9150);
    }

    public Proxy getProxy() {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9150));
    }
}
