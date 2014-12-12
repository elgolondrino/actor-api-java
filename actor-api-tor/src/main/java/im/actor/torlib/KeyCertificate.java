package im.actor.torlib;

import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.IPv4Address;
import im.actor.torlib.data.Timestamp;

import java.io.IOException;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class KeyCertificate extends BserObject {
    private IPv4Address directoryAddress;
    private int directoryPort;
    private HexDigest fingerprint;
    private TorPublicKey identityKey;
    private Timestamp keyPublished;
    private Timestamp keyExpires;
    private TorPublicKey signingKey;
    private String rawDocumentData;

    private boolean hasValidSignature = false;

    @Override
    public void parse(BserValues values) throws IOException {

    }

    @Override
    public void serialize(BserWriter writer) throws IOException {

    }
}
