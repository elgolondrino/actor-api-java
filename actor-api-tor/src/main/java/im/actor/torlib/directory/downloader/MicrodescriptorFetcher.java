package im.actor.torlib.directory.downloader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import im.actor.torlib.Descriptor;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.directory.parsing.DocumentParser;

public class MicrodescriptorFetcher extends DocumentFetcher<Descriptor>{

	private final List<HexDigest> fingerprints;
	
	public MicrodescriptorFetcher(Collection<HexDigest> fingerprints) {
		this.fingerprints = new ArrayList<HexDigest>(fingerprints);
	}

	@Override
	public String getRequestPath() {
		return "/tor/micro/d/"+ fingerprintsToRequestString();
	}
	
	private String fingerprintsToRequestString() {
		final StringBuilder sb = new StringBuilder();
		for(HexDigest fp: fingerprints) {
			appendFingerprint(sb, fp);
		}
		return sb.toString();
	}

	private void appendFingerprint(StringBuilder sb, HexDigest fp) {
		if(sb.length() > 0) {
			sb.append("-");
		}
		sb.append(fp.toBase64(true));
	}

	@Override
	public DocumentParser<Descriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterMicrodescriptorParser(response);
	}
}
