package im.actor.torlib.directory.routers;

import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.cache.DescriptorCache;
import im.actor.torlib.directory.parsing.DocumentParser;
import im.actor.torlib.directory.parsing.DocumentParserFactory;
import im.actor.torlib.directory.parsing.DocumentParserFactoryImpl;
import im.actor.torlib.directory.storage.DirectoryStorage;
import im.actor.torlib.documents.DescriptorDocument;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by ex3ndr on 13.12.14.
 */
public class RouterDescriptors {

    private final static DocumentParserFactory PARSER_FACTORY = new DocumentParserFactoryImpl();

    private final DescriptorCache<DescriptorDocument> descriptorCache;

    public RouterDescriptors(DirectoryStorage store) {
        this.descriptorCache = new DescriptorCache<DescriptorDocument>(store, DirectoryStorage.CacheFile.MICRODESCRIPTOR_CACHE, DirectoryStorage.CacheFile.MICRODESCRIPTOR_JOURNAL) {
            @Override
            protected DocumentParser<DescriptorDocument> createDocumentParser(ByteBuffer buffer) {
                return PARSER_FACTORY.createRouterDescriptorParser(buffer);
            }
        };
    }

    public void load() {
        descriptorCache.initialLoad();
    }

    public void close() {
        descriptorCache.shutdown();
    }

    public DescriptorDocument getDescriptorForRouterStatus(RouterStatus rs) {
        return descriptorCache.getDescriptor(rs.getMicrodescriptorDigest());
    }

    public synchronized void addRouterDescriptors(List<DescriptorDocument> descriptorDocuments) {
        descriptorCache.addDescriptors(descriptorDocuments);
    }

    public DescriptorDocument getDescriptorFromCache(HexDigest descriptorDigest) {
        return descriptorCache.getDescriptor(descriptorDigest);
    }
}
