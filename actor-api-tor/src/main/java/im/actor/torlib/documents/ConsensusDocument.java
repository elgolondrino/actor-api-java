package im.actor.torlib.documents;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Logger;

import im.actor.torlib.crypto.TorPublicKey;
import im.actor.torlib.crypto.TorSignature;
import im.actor.torlib.data.HexDigest;
import im.actor.torlib.data.Timestamp;
import im.actor.torlib.directory.parsing.consensus.VoteAuthorityEntry;
import im.actor.torlib.directory.routers.DirectoryServer;
import im.actor.torlib.directory.TrustedAuthorities;
import im.actor.torlib.directory.parsing.consensus.DirectorySignature;
import im.actor.torlib.directory.parsing.consensus.RequiredCertificateImpl;
import im.actor.torlib.utils.Tor;

public class ConsensusDocument implements Document {

    public enum SignatureStatus {STATUS_VERIFIED, STATUS_FAILED, STATUS_NEED_CERTS}

    public interface RequiredCertificate {

        HexDigest getAuthorityIdentity();

        HexDigest getSigningKey();
    }

    private final static Logger logger = Logger.getLogger(ConsensusDocument.class.getName());

    private final static String BW_WEIGHT_SCALE_PARAM = "bwweightscale";
    private final static int BW_WEIGHT_SCALE_DEFAULT = 10000;
    private final static int BW_WEIGHT_SCALE_MIN = 1;
    private final static int BW_WEIGHT_SCALE_MAX = Integer.MAX_VALUE;

    private final static String CIRCWINDOW_PARAM = "circwindow";
    private final static int CIRCWINDOW_DEFAULT = 1000;
    private final static int CIRCWINDOW_MIN = 100;
    private final static int CIRCWINDOW_MAX = 1000;

    private Set<RequiredCertificate> requiredCertificates = new HashSet<RequiredCertificate>();

    private Timestamp validAfter;
    private Timestamp freshUntil;
    private Timestamp validUntil;
    private int distDelaySeconds;
    private int voteDelaySeconds;
    private Set<String> knownFlags;
    private HexDigest signingHash;
    private HexDigest signingHash256;
    private Map<HexDigest, VoteAuthorityEntry> voteAuthorityEntries;
    private List<RouterStatusDocument> routerStatusEntries;
    private Map<String, Integer> bandwidthWeights;
    private Map<String, Integer> parameters;
    private int signatureCount;
    private boolean isFirstCallToVerifySignatures = true;
    private String rawDocumentData;


    public ConsensusDocument() {
        knownFlags = new HashSet<String>();
        voteAuthorityEntries = new HashMap<HexDigest, VoteAuthorityEntry>();
        routerStatusEntries = new ArrayList<RouterStatusDocument>();
        bandwidthWeights = new HashMap<String, Integer>();
        parameters = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getBandwidthWeights() {
        return bandwidthWeights;
    }

    public void setValidAfter(Timestamp ts) {
        validAfter = ts;
    }

    public void setFreshUntil(Timestamp ts) {
        freshUntil = ts;
    }

    public void setValidUntil(Timestamp ts) {
        validUntil = ts;
    }

    public void setDistDelaySeconds(int seconds) {
        distDelaySeconds = seconds;
    }

    public void setVoteDelaySeconds(int seconds) {
        voteDelaySeconds = seconds;
    }

    public void addParameter(String name, int value) {
        parameters.put(name, value);
    }

    public void addBandwidthWeight(String name, int value) {
        bandwidthWeights.put(name, value);
    }

    public void addSignature(DirectorySignature signature) {
        final VoteAuthorityEntry voteAuthority = voteAuthorityEntries.get(signature.getIdentityDigest());
        if (voteAuthority == null) {
            logger.warning("Consensus contains signature for source not declared in authority section: " + signature.getIdentityDigest());
            return;
        }
        final List<DirectorySignature> signatures = voteAuthority.getSignatures();
        final TorSignature.DigestAlgorithm newSignatureAlgorithm = signature.getSignature().getDigestAlgorithm();
        for (DirectorySignature sig : signatures) {
            TorSignature.DigestAlgorithm algo = sig.getSignature().getDigestAlgorithm();
            if (algo.equals(newSignatureAlgorithm)) {
                logger.warning("Consensus contains two or more signatures for same source with same algorithm");
                return;
            }
        }
        signatureCount += 1;
        signatures.add(signature);
    }

    public void setSigningHash(HexDigest hash) {
        signingHash = hash;
    }

    public void setSigningHash256(HexDigest hash) {
        signingHash256 = hash;
    }

    public void setRawDocumentData(String rawData) {
        rawDocumentData = rawData;
    }

    public void addKnownFlag(String flag) {
        knownFlags.add(flag);
    }

    public void addVoteAuthorityEntry(VoteAuthorityEntry entry) {
        voteAuthorityEntries.put(entry.getIdentity(), entry);
    }

    public void addRouterStatusEntry(RouterStatusDocument entry) {
        routerStatusEntries.add(entry);
    }

    public Timestamp getValidAfterTime() {
        return validAfter;
    }

    public Timestamp getFreshUntilTime() {
        return freshUntil;
    }

    public Timestamp getValidUntilTime() {
        return validUntil;
    }

    public boolean isLive() {
        if (validUntil == null) {
            return false;
        } else {
            return !validUntil.hasPassed();
        }
    }

    public List<RouterStatusDocument> getRouterStatusEntries() {
        return Collections.unmodifiableList(routerStatusEntries);
    }

    public String getRawDocumentData() {
        return rawDocumentData;
    }

    public ByteBuffer getRawDocumentBytes() {
        if (getRawDocumentData() == null) {
            return ByteBuffer.allocate(0);
        } else {
            return ByteBuffer.wrap(getRawDocumentData().getBytes(Tor.getDefaultCharset()));
        }
    }

    public boolean isValidDocument() {
        return (validAfter != null) && (freshUntil != null) && (validUntil != null) &&
                (voteDelaySeconds > 0) && (distDelaySeconds > 0) && (signingHash != null) &&
                (signatureCount > 0);
    }

    public HexDigest getSigningHash() {
        return signingHash;
    }

    public HexDigest getSigningHash256() {
        return signingHash256;
    }

    public synchronized SignatureStatus verifySignatures() {
        boolean firstCall = isFirstCallToVerifySignatures;
        isFirstCallToVerifySignatures = false;
        requiredCertificates.clear();
        int verifiedCount = 0;
        int certsNeededCount = 0;
        final int v3Count = TrustedAuthorities.getInstance().getV3AuthorityServerCount();
        final int required = (v3Count / 2) + 1;

        for (VoteAuthorityEntry entry : voteAuthorityEntries.values()) {
            switch (verifySingleAuthority(entry)) {
                case STATUS_FAILED:
                    break;
                case STATUS_NEED_CERTS:
                    certsNeededCount += 1;
                    break;
                case STATUS_VERIFIED:
                    verifiedCount += 1;
                    break;
            }
        }

        if (verifiedCount >= required) {
            return SignatureStatus.STATUS_VERIFIED;
        } else if (verifiedCount + certsNeededCount >= required) {
            if (firstCall) {
                logger.info("Certificates need to be retrieved to verify consensus");
            }
            return SignatureStatus.STATUS_NEED_CERTS;
        } else {
            return SignatureStatus.STATUS_FAILED;
        }
    }

    private SignatureStatus verifySingleAuthority(VoteAuthorityEntry authority) {

        boolean certsNeeded = false;
        boolean validSignature = false;

        for (DirectorySignature s : authority.getSignatures()) {
            DirectoryServer trusted = TrustedAuthorities.getInstance().getAuthorityServerByIdentity(s.getIdentityDigest());
            if (trusted == null) {
                logger.warning("Consensus signed by unrecognized directory authority: " + s.getIdentityDigest());
                return SignatureStatus.STATUS_FAILED;
            } else {
                switch (verifySignatureForTrustedAuthority(trusted, s)) {
                    case STATUS_NEED_CERTS:
                        certsNeeded = true;
                        break;
                    case STATUS_VERIFIED:
                        validSignature = true;
                        break;
                    default:
                        break;
                }
            }
        }

        if (validSignature) {
            return SignatureStatus.STATUS_VERIFIED;
        } else if (certsNeeded) {
            return SignatureStatus.STATUS_NEED_CERTS;
        } else {
            return SignatureStatus.STATUS_FAILED;
        }
    }

    private SignatureStatus verifySignatureForTrustedAuthority(DirectoryServer trustedAuthority, DirectorySignature signature) {
        final KeyCertificateDocument certificate = trustedAuthority.getCertificateByFingerprint(signature.getSigningKeyDigest());
        if (certificate == null) {
            logger.fine("Missing certificate for signing key: " + signature.getSigningKeyDigest());
            addRequiredCertificateForSignature(signature);
            return SignatureStatus.STATUS_NEED_CERTS;
        }
        if (certificate.isExpired()) {
            return SignatureStatus.STATUS_FAILED;
        }

        final TorPublicKey signingKey = certificate.getAuthoritySigningKey();
        final HexDigest d = (signature.useSha256()) ? signingHash256 : signingHash;
        if (!signingKey.verifySignature(signature.getSignature(), d)) {
            logger.warning("Signature failed on consensus for signing key: " + signature.getSigningKeyDigest());
            return SignatureStatus.STATUS_FAILED;
        }
        return SignatureStatus.STATUS_VERIFIED;
    }

    public Set<RequiredCertificate> getRequiredCertificates() {
        return requiredCertificates;
    }

    private void addRequiredCertificateForSignature(DirectorySignature signature) {
        requiredCertificates.add(new RequiredCertificateImpl(signature.getIdentityDigest(), signature.getSigningKeyDigest()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof ConsensusDocument))
            return false;
        final ConsensusDocument other = (ConsensusDocument) o;
        return other.getSigningHash().equals(signingHash);
    }

    public int hashCode() {
        return (signingHash == null) ? 0 : signingHash.hashCode();
    }

    private int getParameterValue(String name, int defaultValue, int minValue, int maxValue) {
        if (!parameters.containsKey(name)) {
            return defaultValue;
        }
        final int value = parameters.get(name);
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            return value;
        }
    }

    public int getCircWindowParameter() {
        return getParameterValue(CIRCWINDOW_PARAM, CIRCWINDOW_DEFAULT, CIRCWINDOW_MIN, CIRCWINDOW_MAX);
    }

    public int getWeightScaleParameter() {
        return getParameterValue(BW_WEIGHT_SCALE_PARAM, BW_WEIGHT_SCALE_DEFAULT, BW_WEIGHT_SCALE_MIN, BW_WEIGHT_SCALE_MAX);
    }

    public int getBandwidthWeight(String tag) {
        if (bandwidthWeights.containsKey(tag)) {
            return bandwidthWeights.get(tag);
        } else {
            return -1;
        }
    }
}
