package im.actor.tor;

import im.actor.torlib.TorClient;

import java.io.File;

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
        this.torClient.enableSocksListener(9152);
    }
}
