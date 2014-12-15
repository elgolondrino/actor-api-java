package im.actor.torlib.circuits;

import java.util.logging.Logger;

import im.actor.torlib.circuits.build.NTorCircuitExtender;
import im.actor.torlib.circuits.cells.Cell;
import im.actor.torlib.circuits.cells.RelayCell;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.TorException;
import im.actor.torlib.crypto.TorCreateFastKeyAgreement;
import im.actor.torlib.crypto.TorKeyAgreement;
import im.actor.torlib.crypto.TorMessageDigest;
import im.actor.torlib.crypto.TorStreamCipher;

public class CircuitExtender {
    private final static Logger logger = Logger.getLogger(CircuitExtender.class.getName());

    private final static int DH_BYTES = 1024 / 8;
    private final static int PKCS1_OAEP_PADDING_OVERHEAD = 42;
    private final static int CIPHER_KEY_LEN = TorStreamCipher.KEY_LEN;
    public final static int TAP_ONIONSKIN_LEN = PKCS1_OAEP_PADDING_OVERHEAD + CIPHER_KEY_LEN + DH_BYTES;
    public final static int TAP_ONIONSKIN_REPLY_LEN = DH_BYTES + TorMessageDigest.TOR_DIGEST_SIZE;


    private final CircuitImpl circuit;

    public CircuitExtender(CircuitImpl circuit) {
        this.circuit = circuit;
    }


    public CircuitNode createFastTo(Router targetRouter) {
        logger.fine("Creating 'fast' to " + targetRouter);
        final TorCreateFastKeyAgreement kex = new TorCreateFastKeyAgreement();
        sendCreateFastCell(kex);
        return receiveAndProcessCreateFastResponse(targetRouter, kex);
    }

    private void sendCreateFastCell(TorCreateFastKeyAgreement kex) {
        final Cell cell = Cell.createCell(circuit.getCircuitId(), Cell.CREATE_FAST);
        cell.putByteArray(kex.createOnionSkin());
        circuit.sendCell(cell);
    }

    private CircuitNode receiveAndProcessCreateFastResponse(Router targetRouter, TorKeyAgreement kex) {
        final Cell cell = circuit.receiveControlCellResponse();
        if (cell == null) {
            throw new TorException("Timeout building circuit waiting for CREATE_FAST response from " + targetRouter);
        }

        return processCreatedFastCell(targetRouter, cell, kex);
    }

    private CircuitNode processCreatedFastCell(Router targetRouter, Cell cell, TorKeyAgreement kex) {
        final byte[] payload = new byte[TorMessageDigest.TOR_DIGEST_SIZE * 2];
        final byte[] keyMaterial = new byte[CircuitNodeCryptoState.KEY_MATERIAL_SIZE];
        final byte[] verifyHash = new byte[TorMessageDigest.TOR_DIGEST_SIZE];
        cell.getByteArray(payload);
        if (!kex.deriveKeysFromHandshakeResponse(payload, keyMaterial, verifyHash)) {
            // XXX
            return null;
        }
        final CircuitNode node = CircuitNodeImpl.createFirstHop(targetRouter, keyMaterial, verifyHash);
        circuit.appendNode(node);
        return node;
    }

    public CircuitNode extendTo(Router targetRouter) {
        if (circuit.getCircuitLength() == 0) {
            throw new TorException("Cannot EXTEND an empty circuit");
        }

        final NTorCircuitExtender nce = new NTorCircuitExtender(this, targetRouter);
        return nce.extendTo();
    }

    private void logProtocolViolation(String sourceName, Router targetRouter) {
        final String version = (targetRouter == null) ? "(none)" : targetRouter.getVersion();
        final String targetName = (targetRouter == null) ? "(none)" : targetRouter.getNickname();
        logger.warning("Protocol error extending circuit from (" + sourceName + ") to (" + targetName + ") [version: " + version + "]");
    }

    private String nodeToName(CircuitNode node) {
        if (node == null || node.getRouter() == null) {
            return "(null)";
        }
        final Router router = node.getRouter();
        return router.getNickname();
    }


    public void sendRelayCell(RelayCell cell) {
        circuit.sendRelayCell(cell);
    }


    public RelayCell receiveRelayResponse(int expectedCommand, Router extendTarget) {
        final RelayCell cell = circuit.receiveRelayCell();
        if (cell == null) {
            throw new TorException("Timeout building circuit");
        }
        final int command = cell.getRelayCommand();
        if (command == RelayCell.RELAY_TRUNCATED) {
            final int code = cell.getByte() & 0xFF;
            final String msg = Cell.errorToDescription(code);
            final String source = nodeToName(cell.getCircuitNode());
            if (code == Cell.ERROR_PROTOCOL) {
                logProtocolViolation(source, extendTarget);
            }
            throw new TorException("Error from (" + source + ") while extending to (" + extendTarget.getNickname() + "): " + msg);
        } else if (command != expectedCommand) {
            final String expected = RelayCell.commandToDescription(expectedCommand);
            final String received = RelayCell.commandToDescription(command);
            throw new TorException("Received incorrect extend response, expecting " + expected + " but received " + received);
        } else {
            return cell;
        }
    }


    public CircuitNode createNewNode(Router r, byte[] keyMaterial, byte[] verifyDigest) {
        final CircuitNode node = CircuitNodeImpl.createNode(r, circuit.getFinalCircuitNode(), keyMaterial, verifyDigest);
        logger.fine("Adding new circuit node for " + r.getNickname());
        circuit.appendNode(node);
        return node;

    }

    public RelayCell createRelayCell(int command) {
        return new RelayCell(circuit.getFinalCircuitNode(), circuit.getCircuitId(), 0, command, true);
    }

    public Router getFinalRouter() {
        final CircuitNode node = circuit.getFinalCircuitNode();
        if (node != null) {
            return node.getRouter();
        } else {
            return null;
        }
    }
}
