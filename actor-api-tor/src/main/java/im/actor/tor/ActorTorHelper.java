package im.actor.tor;

import im.actor.torlib.TorClient;

import java.io.File;

/**
 * Created by ex3ndr on 08.12.14.
 */
public class ActorTorHelper {
    private TorClient torClient;

    public ActorTorHelper(String path) {
        File file = new File(path);
        file.mkdirs();
        this.torClient = new TorClient(path);
    }

    public TorClient getTorClient() {
        return torClient;
    }

    public void start(int port) {
        this.torClient.start();
        this.torClient.enableSocksListener(port);
    }
}
