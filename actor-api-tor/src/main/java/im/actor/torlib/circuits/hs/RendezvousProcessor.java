package im.actor.torlib.circuits.hs;

import java.math.BigInteger;
import java.util.logging.Logger;

import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.build.InternalCircuitMutate;
import im.actor.torlib.circuits.cells.Cell;
import im.actor.torlib.circuits.cells.RelayCell;
import im.actor.torlib.circuits.streams.CircuitNode;
import im.actor.torlib.circuits.streams.CircuitNodeCryptoState;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.crypto.TorMessageDigest;
import im.actor.torlib.crypto.TorRandom;
import im.actor.torlib.crypto.TorTapKeyAgreement;
import im.actor.utils.HexDigest;

public class RendezvousProcessor {
    private final static Logger logger = Logger.getLogger(RendezvousProcessor.class.getName());

    private final static int RENDEZVOUS_COOKIE_LEN = 20;
    private final static TorRandom random = new TorRandom();

    private final Circuit circuit;
    private final byte[] cookie;

    protected RendezvousProcessor(Circuit circuit) {
        this.circuit = circuit;
        this.cookie = random.getBytes(RENDEZVOUS_COOKIE_LEN);
    }

    boolean establishRendezvous() {
        final RelayCell cell = circuit.createRelayCell(RelayCell.RELAY_COMMAND_ESTABLISH_RENDEZVOUS, 0, circuit.getFinalCircuitNode());
        cell.putByteArray(cookie);
        circuit.sendRelayCell(cell);
        final RelayCell response = circuit.receiveRelayCell();
        if (response == null) {
            logger.info("Timeout waiting for Rendezvous establish response");
            return false;
        } else if (response.getRelayCommand() != RelayCell.RELAY_COMMAND_RENDEZVOUS_ESTABLISHED) {
            logger.info("Response received from Rendezvous establish was not expected acknowledgement, Relay Command: " + response.getRelayCommand());
            return false;
        } else {
            return true;
        }
    }

    Circuit processRendezvous2(TorTapKeyAgreement kex) {
        final RelayCell cell = circuit.receiveRelayCell();
        if (cell == null) {
            logger.info("Timeout waiting for RENDEZVOUS2");
            return null;
        } else if (cell.getRelayCommand() != RelayCell.RELAY_COMMAND_RENDEZVOUS2) {
            logger.info("Unexpected Relay cell type received while waiting for RENDEZVOUS2: " + cell.getRelayCommand());
            return null;
        }
        final BigInteger peerPublic = readPeerPublic(cell);
        final HexDigest handshakeDigest = readHandshakeDigest(cell);
        if (peerPublic == null || handshakeDigest == null) {
            return null;
        }
        final byte[] verifyHash = new byte[TorMessageDigest.TOR_DIGEST_SIZE];
        final byte[] keyMaterial = new byte[CircuitNodeCryptoState.KEY_MATERIAL_SIZE];
        if (!kex.deriveKeysFromDHPublicAndHash(peerPublic, handshakeDigest.getRawBytes(), keyMaterial, verifyHash)) {
            logger.info("Error deriving session keys while extending to hidden service");
            return null;
        }

        InternalCircuitMutate.connectHiddenService(circuit, CircuitNode.createAnonymous(circuit.getFinalCircuitNode(), keyMaterial, verifyHash));
        return circuit;
    }

    private BigInteger readPeerPublic(Cell cell) {
        final byte[] dhPublic = new byte[TorTapKeyAgreement.DH_LEN];
        cell.getByteArray(dhPublic);
        final BigInteger peerPublic = new BigInteger(1, dhPublic);
        if (!TorTapKeyAgreement.isValidPublicValue(peerPublic)) {
            logger.warning("Illegal DH public value received: " + peerPublic);
            return null;
        }
        return peerPublic;
    }

    HexDigest readHandshakeDigest(Cell cell) {
        final byte[] digestBytes = new byte[TorMessageDigest.TOR_DIGEST_SIZE];
        cell.getByteArray(digestBytes);
        return HexDigest.createFromDigestBytes(digestBytes);
    }


    byte[] getCookie() {
        return cookie;
    }

    Router getRendezvousRouter() {
        return circuit.getFinalCircuitNode().getRouter();
    }
}
