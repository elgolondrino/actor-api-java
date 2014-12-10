package im.actor.torlib;

import java.nio.ByteBuffer;

public interface Document {
	ByteBuffer getRawDocumentBytes();
	String getRawDocumentData();
	boolean isValidDocument();
}
