package im.actor.torlib.circuits;

import java.io.IOException;
import java.io.OutputStream;

import im.actor.torlib.RelayCell;
import im.actor.torlib.circuits.cells.RelayCellImpl;

public class TorOutputStream extends OutputStream {

    private final TorStream torStream;
    private RelayCell currentOutputCell;
    private volatile boolean isClosed;
    private long bytesSent;

    public TorOutputStream(TorStream torStream) {
        this.torStream = torStream;
        this.bytesSent = 0;
    }

    private void flushCurrentOutputCell() {
        if (currentOutputCell != null && currentOutputCell.cellBytesConsumed() > RelayCell.HEADER_SIZE) {
            torStream.waitForSendWindowAndDecrement();
            torStream.getCircuit().sendRelayCell(currentOutputCell);
            if (currentOutputCell != null) {
                bytesSent += (currentOutputCell.cellBytesConsumed() - RelayCell.HEADER_SIZE);
            }
        }

        currentOutputCell = new RelayCellImpl(torStream.getTargetNode(), torStream.getCircuit().getCircuitId(),
                torStream.getStreamId(), RelayCell.RELAY_DATA);
    }

    public  long getBytesSent() {
        return bytesSent;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        checkOpen();
        if (currentOutputCell == null || currentOutputCell.cellBytesRemaining() == 0)
            flushCurrentOutputCell();
        currentOutputCell.putByte(b);
    }

    public synchronized void write(byte[] data, int offset, int length) throws IOException {
        checkOpen();
        if (currentOutputCell == null || currentOutputCell.cellBytesRemaining() == 0)
            flushCurrentOutputCell();

        while (length > 0) {
            if (length < currentOutputCell.cellBytesRemaining()) {
                currentOutputCell.putByteArray(data, offset, length);
                return;
            }
            final int writeCount = currentOutputCell.cellBytesRemaining();
            currentOutputCell.putByteArray(data, offset, writeCount);
            flushCurrentOutputCell();
            offset += writeCount;
            length -= writeCount;
        }
    }

    private void checkOpen() throws IOException {
        if (isClosed)
            throw new IOException("Output stream is closed");
    }

    public synchronized void flush() {
        if (isClosed)
            return;
        flushCurrentOutputCell();
    }

    public synchronized void close() {
        if (isClosed)
            return;
        flush();
        isClosed = true;
        currentOutputCell = null;
        torStream.close();
    }

    public String toString() {
        return "TorOutputStream stream=" + torStream.getStreamId() + " node=" + torStream.getTargetNode();
    }
}
