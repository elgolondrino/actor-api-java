package im.actor.torlib.directory.routers;

import im.actor.torlib.GuardEntry;
import im.actor.torlib.Router;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.directory.storage.StateFile;

import java.util.List;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class Guards {
    private DirectoryStorage store;
    private StateFile stateFile;

    public Guards(NewDirectory newDirectory) {
        this.store = newDirectory.getStore();
        this.stateFile = new StateFile(store, newDirectory);
    }

    public void load(){
        stateFile.parseBuffer(store.loadCacheFile(DirectoryStorage.CacheFile.STATE));
    }

    public GuardEntry createGuardEntryFor(Router router) {
        return stateFile.createGuardEntryFor(router);
    }

    public List<GuardEntry> getGuardEntries() {
        return stateFile.getGuardEntries();
    }

    public void removeGuardEntry(GuardEntry entry) {
        stateFile.removeGuardEntry(entry);
    }

    public void addGuardEntry(GuardEntry entry) {
        stateFile.addGuardEntry(entry);
    }
}
