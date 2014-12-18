package im.actor.torlib.circuits;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import im.actor.torlib.circuits.cells.Cell;
import im.actor.torlib.circuits.streams.CircuitIO;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.connections.Connection;
import im.actor.torlib.circuits.cells.RelayCell;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.TorException;

/**
 * This class represents an established circuit through the Tor network.
 */
public abstract class Circuit {
    protected final static Logger LOG = Logger.getLogger(Circuit.class.getName());

    private final CircuitManager circuitManager;
    protected final List<Router> path;

    private final List<CircuitNode> nodeList;
    private final CircuitStatus status;

    private CircuitIO io;

    protected Circuit(List<Router> path, Connection connection, CircuitManager circuitManager) {
        this.nodeList = new ArrayList<CircuitNode>();
        int id = connection.bindCircuit(this);
        io = new CircuitIO(this, connection, id);
        this.circuitManager = circuitManager;
        this.path = path;
        this.status = new CircuitStatus();
    }


    public List<Router> getPath() {
        return path;
    }

    public Router getFirstRouter() {
        return path.get(0);
    }

    public Router getLastRouter() {
        return path.get(path.size() - 1);
    }

    public CircuitStatus getStatus() {
        return status;
    }

    public boolean isConnected() {
        return status.isConnected();
    }

    public boolean isClean() {
        return !status.isDirty();
    }

    public int getSecondsDirty() {
        return (int) (status.getMillisecondsDirty() / 1000);
    }

    public int getCircuitId() {
        if (io == null) {
            return 0;
        } else {
            return io.getCircuitId();
        }
    }

    public Connection getConnection() {
        if (!isConnected())
            throw new TorException("Circuit is not connected.");
        return io.getConnection();
    }

    public void appendNode(CircuitNode node) {
        nodeList.add(node);
    }

    public List<CircuitNode> getNodeList() {
        return nodeList;
    }

    public int getCircuitLength() {
        return nodeList.size();
    }

    public CircuitNode getFinalCircuitNode() {
        if (nodeList.isEmpty())
            throw new TorException("getFinalCircuitNode() called on empty circuit");
        return nodeList.get(getCircuitLength() - 1);
    }

    // Cells

    public void sendRelayCell(RelayCell cell) {
        io.sendRelayCellTo(cell, cell.getCircuitNode());
    }

    public void sendRelayCellToFinalNode(RelayCell cell) {
        io.sendRelayCellTo(cell, getFinalCircuitNode());
    }

    public RelayCell createRelayCell(int relayCommand, int streamId, CircuitNode targetNode) {
        return io.createRelayCell(relayCommand, streamId, targetNode);
    }

    public RelayCell receiveRelayCell() {
        return io.dequeueRelayResponseCell();
    }

    public void sendCell(Cell cell) {
        io.sendCell(cell);
    }

    public Cell receiveControlCellResponse() {
        return io.receiveControlCellResponse();
    }

    public void deliverControlCell(Cell cell) {
        io.deliverControlCell(cell);
    }

    public void deliverRelayCell(Cell cell) {
        io.deliverRelayCell(cell);
    }


    public TorStream createNewStream(boolean autoclose) {
        return io.createNewStream(autoclose);
    }

    public TorStream createNewStream() {
        return createNewStream(false);
    }

    public void removeStream(TorStream torStream) {
        io.removeStream(torStream);
    }


    public void setStateDestroyed() {
        status.destroy();
        circuitManager.getExitActiveCircuits().removeActiveCircuit(this);
    }


    public void destroyCircuit() {
        // We might not have bound this circuit yet
        if (io != null) {
            io.destroyCircuit();
        }
        circuitManager.getExitActiveCircuits().removeActiveCircuit(this);
    }

    public void markForClose() {
        if (io != null) {
            io.markForClose();
        }
    }

    public boolean isMarkedForClose() {
        if (io == null) {
            return false;
        } else {
            return io.isMarkedForClose();
        }
    }
}
