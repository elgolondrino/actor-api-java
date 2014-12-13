package im.actor.torlib.directory;

import im.actor.torlib.Router;
import im.actor.torlib.TorConfig;
import im.actor.torlib.crypto.TorRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class NewDirectory {
    private final static AtomicInteger NEXT_ID = new AtomicInteger(1);

    private static final TorRandom RANDOM = new TorRandom();

    private final int id;
    private final TorConfig config;

    private final Directory obsoleteDirectory;

    public NewDirectory(Directory obsoleteDirectory, TorConfig config) {
        this.id = NEXT_ID.getAndIncrement();
        this.obsoleteDirectory = obsoleteDirectory;
        this.config = config;
    }

    public Directory getObsoleteDirectory() {
        return obsoleteDirectory;
    }

    public ArrayList<Router> pickInternalPath(Router endNode) {
        return new ArrayList<Router>();
    }

    public ArrayList<Router> pickExternalPath(Router endNode) {
        return new ArrayList<Router>();
    }

    public DirectoryServer pickAuthority() {
        final List<DirectoryServer> servers = TrustedAuthorities.getInstance().getAuthorityServers();
        final int idx = RANDOM.nextInt(servers.size());
        return servers.get(idx);
    }
}
