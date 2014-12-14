package im.actor.torlib.documents.downloader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import im.actor.torlib.documents.DescriptorDocument;
import im.actor.utils.HexDigest;
import im.actor.torlib.directory.parsing.DocumentParser;

public class DescriptorFetcher extends DocumentFetcher<DescriptorDocument> {

    private final List<HexDigest> fingerprints;

    public DescriptorFetcher(Collection<HexDigest> fingerprints) {
        this.fingerprints = new ArrayList<HexDigest>(fingerprints);
    }

    @Override
    public String getRequestPath() {
        return "/tor/micro/d/" + fingerprintsToRequestString();
    }

    private String fingerprintsToRequestString() {
        final StringBuilder sb = new StringBuilder();
        for (HexDigest fp : fingerprints) {
            appendFingerprint(sb, fp);
        }
        return sb.toString();
    }

    private void appendFingerprint(StringBuilder sb, HexDigest fp) {
        if (sb.length() > 0) {
            sb.append("-");
        }
        sb.append(fp.toBase64(true));
    }

    @Override
    public DocumentParser<DescriptorDocument> createParser(ByteBuffer response) {
        return PARSER_FACTORY.createRouterDescriptorParser(response);
    }
}
