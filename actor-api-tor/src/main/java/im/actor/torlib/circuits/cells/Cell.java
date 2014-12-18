package im.actor.torlib.circuits.cells;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Cell {

    /**
     * Command constant for a PADDING type cell.
     */
    public final static int PADDING = 0;

    /**
     * Command constant for a CREATE type cell.
     */
    public final static int CREATE = 1;

    /**
     * Command constant for a CREATED type cell.
     */
    public final static int CREATED = 2;

    /**
     * Command constant for a RELAY type cell.
     */
    public final static int RELAY = 3;

    /**
     * Command constant for a DESTROY type cell.
     */
    public final static int DESTROY = 4;

    /**
     * Command constant for a CREATE_FAST type cell.
     */
    public final static int CREATE_FAST = 5;

    /**
     * Command constant for a CREATED_FAST type cell.
     */
    public final static int CREATED_FAST = 6;

    /**
     * Command constant for a VERSIONS type cell.
     */
    public final static int VERSIONS = 7;

    /**
     * Command constant for a NETINFO type cell.
     */
    public final static int NETINFO = 8;

    /**
     * Command constant for a RELAY_EARLY type cell.
     */
    public final static int RELAY_EARLY = 9;

    public final static int VPADDING = 128;
    public final static int CERTS = 129;
    public final static int AUTH_CHALLENGE = 130;
    public final static int AUTHENTICATE = 131;
    public final static int AUTHORIZE = 132;

    public final static int ERROR_NONE = 0;
    public final static int ERROR_PROTOCOL = 1;
    public final static int ERROR_INTERNAL = 2;
    public final static int ERROR_REQUESTED = 3;
    public final static int ERROR_HIBERNATING = 4;
    public final static int ERROR_RESOURCELIMIT = 5;
    public final static int ERROR_CONNECTFAILED = 6;
    public final static int ERROR_OR_IDENTITY = 7;
    public final static int ERROR_OR_CONN_CLOSED = 8;
    public final static int ERROR_FINISHED = 9;
    public final static int ERROR_TIMEOUT = 10;
    public final static int ERROR_DESTROYED = 11;
    public final static int ERROR_NOSUCHSERVICE = 12;

    public final static int ADDRESS_TYPE_HOSTNAME = 0x00;
    public final static int ADDRESS_TYPE_IPV4 = 0x04;
    public final static int ADRESS_TYPE_IPV6 = 0x06;

    /**
     * The fixed size of a standard cell.
     */
    public final static int CELL_LEN = 512;

    /**
     * The length of a standard cell header.
     */
    public final static int CELL_HEADER_LEN = 3;

    /**
     * The header length for a variable length cell (ie: VERSIONS)
     */
    public final static int CELL_VAR_HEADER_LEN = 5;

    /**
     * The length of the payload space in a standard cell.
     */
    public final static int CELL_PAYLOAD_LEN = CELL_LEN - CELL_HEADER_LEN;

    public static Cell createCell(int circuitId, int command) {
        return new Cell(circuitId, command);
    }

    public static Cell createVarCell(int circuitId, int command, int payloadLength) {
        return new Cell(circuitId, command, payloadLength);
    }

    public static Cell readFromInputStream(InputStream input) throws IOException {
        final ByteBuffer header = readHeaderFromInputStream(input);
        final int circuitId = header.getShort() & 0xFFFF;
        final int command = header.get() & 0xFF;

        if (command == VERSIONS || command > 127) {
            return readVarCell(circuitId, command, input);
        }

        final Cell cell = new Cell(circuitId, command);
        readAll(input, cell.getCellBytes(), CELL_HEADER_LEN, CELL_PAYLOAD_LEN);

        return cell;
    }

    private static ByteBuffer readHeaderFromInputStream(InputStream input) throws IOException {
        final byte[] cellHeader = new byte[CELL_HEADER_LEN];
        readAll(input, cellHeader);
        return ByteBuffer.wrap(cellHeader);
    }

    private static Cell readVarCell(int circuitId, int command, InputStream input) throws IOException {
        final byte[] lengthField = new byte[2];
        readAll(input, lengthField);
        final int length = ((lengthField[0] & 0xFF) << 8) | (lengthField[1] & 0xFF);
        Cell cell = new Cell(circuitId, command, length);
        readAll(input, cell.getCellBytes(), CELL_VAR_HEADER_LEN, length);
        return cell;
    }

    private static void readAll(InputStream input, byte[] buffer) throws IOException {
        readAll(input, buffer, 0, buffer.length);
    }

    private static void readAll(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int bytesRead = 0;
        while (bytesRead < length) {
            final int n = input.read(buffer, offset + bytesRead, length - bytesRead);
            if (n == -1)
                throw new EOFException();
            bytesRead += n;
        }
    }

    private final int circuitId;
    private final int command;
    protected final ByteBuffer cellBuffer;

    /* Variable length cell constructor (ie: VERSIONS cells only) */
    private Cell(int circuitId, int command, int payloadLength) {
        this.circuitId = circuitId;
        this.command = command;
        this.cellBuffer = ByteBuffer.wrap(new byte[CELL_VAR_HEADER_LEN + payloadLength]);
        cellBuffer.putShort((short) circuitId);
        cellBuffer.put((byte) command);
        cellBuffer.putShort((short) payloadLength);
        cellBuffer.mark();
    }

    /* Fixed length cell constructor */
    protected Cell(int circuitId, int command) {
        this.circuitId = circuitId;
        this.command = command;
        this.cellBuffer = ByteBuffer.wrap(new byte[CELL_LEN]);
        cellBuffer.putShort((short) circuitId);
        cellBuffer.put((byte) command);
        cellBuffer.mark();
    }

    protected Cell(byte[] rawCell) {
        this.cellBuffer = ByteBuffer.wrap(rawCell);
        this.circuitId = cellBuffer.getShort() & 0xFFFF;
        this.command = cellBuffer.get() & 0xFF;
        cellBuffer.mark();
    }

    public int getCircuitId() {
        return circuitId;
    }

    public int getCommand() {
        return command;
    }

    public void resetToPayload() {
        cellBuffer.reset();
    }

    public int getByte() {
        return cellBuffer.get() & 0xFF;
    }

    public int getByteAt(int index) {
        return cellBuffer.get(index) & 0xFF;
    }

    public int getShort() {
        return cellBuffer.getShort() & 0xFFFF;
    }

    public int getInt() {
        return cellBuffer.getInt();
    }

    public int getShortAt(int index) {
        return cellBuffer.getShort(index) & 0xFFFF;
    }

    public void getByteArray(byte[] buffer) {
        cellBuffer.get(buffer);
    }

    public int cellBytesConsumed() {
        return cellBuffer.position();
    }

    public int cellBytesRemaining() {
        return cellBuffer.remaining();
    }

    public void putByte(int value) {
        cellBuffer.put((byte) value);
    }

    public void putByteAt(int index, int value) {
        cellBuffer.put(index, (byte) value);
    }

    public void putShort(int value) {
        cellBuffer.putShort((short) value);
    }

    public void putShortAt(int index, int value) {
        cellBuffer.putShort(index, (short) value);
    }

    public void putInt(int value) {
        cellBuffer.putInt(value);
    }

    public void putString(String string) {
        final byte[] bytes = new byte[string.length() + 1];
        for (int i = 0; i < string.length(); i++)
            bytes[i] = (byte) string.charAt(i);
        putByteArray(bytes);
    }

    public void putByteArray(byte[] data) {
        cellBuffer.put(data);
    }

    public void putByteArray(byte[] data, int offset, int length) {
        cellBuffer.put(data, offset, length);
    }

    public byte[] getCellBytes() {
        return cellBuffer.array();
    }

    public String toString() {
        return "Cell: circuit_id=" + circuitId + " command=" + command + " payload_len=" + cellBuffer.position();
    }

    public static String errorToDescription(int errorCode) {
        switch (errorCode) {
            case ERROR_NONE:
                return "No error reason given";
            case ERROR_PROTOCOL:
                return "Tor protocol violation";
            case ERROR_INTERNAL:
                return "Internal error";
            case ERROR_REQUESTED:
                return "Response to a TRUNCATE command sent from client";
            case ERROR_HIBERNATING:
                return "Not currently operating; trying to save bandwidth.";
            case ERROR_RESOURCELIMIT:
                return "Out of memory, sockets, or circuit IDs.";
            case ERROR_CONNECTFAILED:
                return "Unable to reach server.";
            case ERROR_OR_IDENTITY:
                return "Connected to server, but its OR identity was not as expected.";
            case ERROR_OR_CONN_CLOSED:
                return "The OR connection that was carrying this circuit died.";
            case ERROR_FINISHED:
                return "The circuit has expired for being dirty or old.";
            case ERROR_TIMEOUT:
                return "Circuit construction took too long.";
            case ERROR_DESTROYED:
                return "The circuit was destroyed without client TRUNCATE";
            case ERROR_NOSUCHSERVICE:
                return "Request for unknown hidden service";
            default:
                return "Error code " + errorCode;
        }
    }
}
