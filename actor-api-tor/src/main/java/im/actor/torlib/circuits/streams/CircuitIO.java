package im.actor.torlib.circuits.streams;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.CircuitStatus;
import im.actor.torlib.circuits.cells.Cell;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.errors.ConnectionIOException;
import im.actor.torlib.circuits.cells.RelayCell;
import im.actor.utils.Threading;
import im.actor.torlib.errors.TorException;

public class CircuitIO {
    private static final Logger logger = Logger.getLogger(CircuitIO.class.getName());
    private final static long CIRCUIT_BUILD_TIMEOUT_MS = 30 * 1000;
    private final static long CIRCUIT_RELAY_RESPONSE_TIMEOUT = 20 * 1000;

    private final Circuit circuit;
    private final Connection connection;
    private final int circuitId;

    private final BlockingQueue<RelayCell> relayCellResponseQueue;
    private final BlockingQueue<Cell> controlCellResponseQueue;
    private final Map<Integer, TorStream> streamMap;
    private final ReentrantLock streamLock = Threading.lock("stream");
    private final ReentrantLock relaySendLock = Threading.lock("relaySend");

    private boolean isMarkedForClose;
    private boolean isClosed;

    public CircuitIO(Circuit circuit, Connection connection, int circuitId) {
        this.circuit = circuit;
        this.connection = connection;
        this.circuitId = circuitId;

        this.relayCellResponseQueue = new LinkedBlockingQueue<RelayCell>();
        this.controlCellResponseQueue = new LinkedBlockingQueue<Cell>();
        this.streamMap = new HashMap<Integer, TorStream>();
    }

    public Connection getConnection() {
        return connection;
    }

    public int getCircuitId() {
        return circuitId;
    }

    public RelayCell dequeueRelayResponseCell() {
        try {
            final long timeout = getReceiveTimeout();
            return relayCellResponseQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private RelayCell decryptRelayCell(Cell cell) {
        for (CircuitNode node : circuit.getNodeList()) {
            if (node.decryptBackwardCell(cell)) {
                return RelayCell.createFromCell(node, cell);
            }
        }
        destroyCircuit();
        throw new TorException("Could not decrypt relay cell");
    }

    // Return null on timeout
    public Cell receiveControlCellResponse() {
        try {
            final long timeout = getReceiveTimeout();
            return controlCellResponseQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }


    private long getReceiveTimeout() {
        return CIRCUIT_RELAY_RESPONSE_TIMEOUT;
    }

    private long remainingBuildTime() {
        final long elapsed = circuit.getStatus().getMillisecondsElapsedSinceCreated();
        if (elapsed == 0 || elapsed >= CIRCUIT_BUILD_TIMEOUT_MS)
            return 0;
        return CIRCUIT_BUILD_TIMEOUT_MS - elapsed;
    }

    /*
     * This is called by the cell reading thread in ConnectionImpl to deliver control cells
     * associated with this circuit (CREATED, CREATED_FAST, or DESTROY).
     */
    public void deliverControlCell(Cell cell) {
        if (cell.getCommand() == Cell.DESTROY) {
            processDestroyCell(cell.getByte());
        } else {
            controlCellResponseQueue.add(cell);
        }
    }

    private void processDestroyCell(int reason) {
        logger.fine("DESTROY cell received (" + Cell.errorToDescription(reason) + ") on " + circuit);
        destroyCircuit();
    }

    /* This is called by the cell reading thread in ConnectionImpl to deliver RELAY cells. */
    public void deliverRelayCell(Cell cell) {
        circuit.getStatus().updateDirtyTimestamp();
        final RelayCell relayCell = decryptRelayCell(cell);
        logRelayCell("Dispatching: ", relayCell);
        switch (relayCell.getRelayCommand()) {
            case RelayCell.RELAY_EXTENDED:
            case RelayCell.RELAY_EXTENDED2:
            case RelayCell.RELAY_RESOLVED:
            case RelayCell.RELAY_TRUNCATED:
            case RelayCell.RELAY_COMMAND_RENDEZVOUS_ESTABLISHED:
            case RelayCell.RELAY_COMMAND_INTRODUCE_ACK:
            case RelayCell.RELAY_COMMAND_RENDEZVOUS2:
                relayCellResponseQueue.add(relayCell);
                break;
            case RelayCell.RELAY_DATA:
            case RelayCell.RELAY_END:
            case RelayCell.RELAY_CONNECTED:
                processRelayDataCell(relayCell);
                break;

            case RelayCell.RELAY_SENDME:
                if (relayCell.getStreamId() != 0)
                    processRelayDataCell(relayCell);
                else
                    processCircuitSendme(relayCell);
                break;
            case RelayCell.RELAY_BEGIN:
            case RelayCell.RELAY_BEGIN_DIR:
            case RelayCell.RELAY_EXTEND:
            case RelayCell.RELAY_RESOLVE:
            case RelayCell.RELAY_TRUNCATE:
                destroyCircuit();
                throw new TorException("Unexpected 'forward' direction relay cell type: " + relayCell.getRelayCommand());
        }
    }

    /* Runs in the context of the connection cell reading thread */
    private void processRelayDataCell(RelayCell cell) {
        if (cell.getRelayCommand() == RelayCell.RELAY_DATA) {
            cell.getCircuitNode().decrementDeliverWindow();
            if (cell.getCircuitNode().considerSendingSendme()) {
                final RelayCell sendme = createRelayCell(RelayCell.RELAY_SENDME, 0, cell.getCircuitNode());
                sendRelayCellTo(sendme, sendme.getCircuitNode());
            }
        }

        streamLock.lock();
        try {
            final TorStream torStream = streamMap.get(cell.getStreamId());
            // It's not unusual for the stream to not be found.  For example, if a RELAY_CONNECTED arrives after
            // the client has stopped waiting for it, the stream will never be tracked and eventually the edge node
            // will send a RELAY_END for this stream.
            if (torStream != null) {
                torStream.addInputCell(cell);
            }
        } finally {
            streamLock.unlock();
        }
    }

    public RelayCell createRelayCell(int relayCommand, int streamId, CircuitNode targetNode) {
        return new RelayCell(targetNode, circuitId, streamId, relayCommand);
    }

    public void sendRelayCellTo(RelayCell cell, CircuitNode targetNode) {
        relaySendLock.lock();
        try {
            logRelayCell("Sending:     ", cell);
            cell.setLength();
            targetNode.updateForwardDigest(cell);
            cell.setDigest(targetNode.getForwardDigestBytes());

            for (CircuitNode node = targetNode; node != null; node = node.getPreviousNode())
                node.encryptForwardCell(cell);

            if (cell.getRelayCommand() == RelayCell.RELAY_DATA)
                targetNode.waitForSendWindowAndDecrement();

            sendCell(cell);
        } finally {
            relaySendLock.unlock();
        }
    }


    private void logRelayCell(String message, RelayCell cell) {
        final Level level = getLogLevelForCell(cell);
        if (!logger.isLoggable(level)) {
            return;
        }
        logger.log(level, message + cell);
    }

    private Level getLogLevelForCell(RelayCell cell) {
        switch (cell.getRelayCommand()) {
            case RelayCell.RELAY_DATA:
            case RelayCell.RELAY_SENDME:
                return Level.FINEST;
            default:
                return Level.FINER;
        }
    }

    public void sendCell(Cell cell) {
        final CircuitStatus status = circuit.getStatus();
        if (!(status.isConnected()))
            return;
        try {
            status.updateDirtyTimestamp();
            connection.sendCell(cell);
        } catch (ConnectionIOException e) {
            destroyCircuit();
        }
    }

    public void markForClose() {
        boolean shouldClose;
        streamLock.lock();
        try {
            if (isMarkedForClose) {
                return;
            }
            isMarkedForClose = true;
            shouldClose = streamMap.isEmpty();
        } finally {
            streamLock.unlock();
        }
        if (shouldClose)
            closeCircuit();
    }

    public boolean isMarkedForClose() {
        streamLock.lock();
        try {
            return isMarkedForClose;
        } finally {
            streamLock.unlock();
        }
    }

    private void closeCircuit() {
        logger.fine("Closing circuit " + circuit);
        sendDestroyCell(Cell.ERROR_NONE);
        connection.removeCircuit(circuit);
        circuit.setStateDestroyed();
        isClosed = true;
    }

    void sendDestroyCell(int reason) {
        Cell destroy = Cell.createCell(circuitId, Cell.DESTROY);
        destroy.putByte(reason);
        try {
            connection.sendCell(destroy);
        } catch (ConnectionIOException e) {
            logger.warning("Connection IO error sending DESTROY cell: " + e.getMessage());
        }
    }

    private void processCircuitSendme(RelayCell cell) {
        cell.getCircuitNode().incrementSendWindow();
    }

    public void destroyCircuit() {
        streamLock.lock();
        try {
            if (isClosed) {
                return;
            }
            circuit.setStateDestroyed();
            connection.removeCircuit(circuit);
            final List<TorStream> tmpList = new ArrayList<TorStream>(streamMap.values());
            for (TorStream s : tmpList) {
                s.close();
            }
            isClosed = true;
        } finally {
            streamLock.unlock();
        }
    }

    public TorStream createNewStream(boolean autoclose) {
        streamLock.lock();
        try {
            // final int streamId = circuit.getStatus().nextStreamId();
            final TorStream torStream = new TorStream(circuit, circuit.getFinalCircuitNode(), autoclose);
            streamMap.put(torStream.getStreamId(), torStream);
            return torStream;
        } finally {
            streamLock.unlock();
        }
    }

    public void removeStream(TorStream torStream) {
        boolean shouldClose;
        streamLock.lock();
        try {
            streamMap.remove(torStream.getStreamId());
            shouldClose = streamMap.isEmpty() && isMarkedForClose;
        } finally {
            streamLock.unlock();
        }
        if (shouldClose)
            closeCircuit();
    }

    public List<TorStream> getActiveStreams() {
        streamLock.lock();
        try {
            return new ArrayList<TorStream>(streamMap.values());
        } finally {
            streamLock.unlock();
        }
    }
}