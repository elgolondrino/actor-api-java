package im.actor.tor;

import com.subgraph.orchid.TorClient;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by ex3ndr on 08.12.14.
 */
public class ActorTorHelper {
    private TorClient torClient;

    public ActorTorHelper(String path) {
        this.torClient = new TorClient();
        File file = new File(path);
        file.mkdirs();
        this.torClient.getConfig().setDataDirectory(file);
    }

    public TorClient getTorClient() {
        return torClient;
    }

    public void start() {
        this.torClient.start();
        this.torClient.enableSocksListener(9151);
    }
}
