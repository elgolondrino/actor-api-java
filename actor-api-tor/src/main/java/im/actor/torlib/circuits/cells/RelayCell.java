package im.actor.torlib.circuits.cells;

import java.nio.ByteBuffer;

import im.actor.torlib.circuits.streams.CircuitNode;
import im.actor.torlib.errors.TorException;

public class RelayCell extends Cell {

	public static RelayCell createFromCell(CircuitNode node, Cell cell) {
		if(cell.getCommand() != RELAY)
			throw new TorException("Attempted to create RelayCell from Cell type: "+ cell.getCommand());
		return new RelayCell(node, cell.getCellBytes());
	}

	public final static int LENGTH_OFFSET = 12;
	public final static int RECOGNIZED_OFFSET = 4;
	public final static int DIGEST_OFFSET = 8;
	public final static int HEADER_SIZE = 14;

	public final static int RELAY_BEGIN = 1;
	public final static int RELAY_DATA = 2;
	public final static int RELAY_END = 3;
	public final static int RELAY_CONNECTED = 4;
	public final static int RELAY_SENDME = 5;
	public final static int RELAY_EXTEND = 6;
	public final static int RELAY_EXTENDED = 7;
	public final static int RELAY_TRUNCATE = 8;
	public final static int RELAY_TRUNCATED = 9;
	public final static int RELAY_DROP = 10;
	public final static int RELAY_RESOLVE = 11;
	public final static int RELAY_RESOLVED = 12;
	public final static int RELAY_BEGIN_DIR = 13;
	public final static int RELAY_EXTEND2 = 14;
	public final static int RELAY_EXTENDED2 = 15;

	public final static int RELAY_COMMAND_ESTABLISH_INTRO = 32;
	public final static int RELAY_COMMAND_ESTABLISH_RENDEZVOUS = 33;
	public final static int RELAY_COMMAND_INTRODUCE1 = 34;
	public final static int RELAY_COMMAND_INTRODUCE2 = 35;
	public final static int RELAY_COMMAND_RENDEZVOUS1 = 36;
	public final static int RELAY_COMMAND_RENDEZVOUS2 = 37;
	public final static int RELAY_COMMAND_INTRO_ESTABLISHED = 38;
	public final static int RELAY_COMMAND_RENDEZVOUS_ESTABLISHED = 39;
	public final static int RELAY_COMMAND_INTRODUCE_ACK = 40;

	public final static int REASON_MISC = 1;
	public final static int REASON_RESOLVEFAILED = 2;
	public final static int REASON_CONNECTREFUSED = 3;
	public final static int REASON_EXITPOLICY = 4;
	public final static int REASON_DESTROY = 5;
	public final static int REASON_DONE = 6;
	public final static int REASON_TIMEOUT = 7;
	public final static int REASON_NOROUTE = 8;
	public final static int REASON_HIBERNATING = 9;
	public final static int REASON_INTERNAL = 10;
	public final static int REASON_RESOURCELIMIT = 11;
	public final static int REASON_CONNRESET = 12;
	public final static int REASON_TORPROTOCOL = 13;
	public final static int REASON_NOTDIRECTORY = 14;

	private final int streamId;
	private final int relayCommand;
	private final CircuitNode circuitNode;
	private final boolean isOutgoing;

	/*
	 * The payload of each unencrypted RELAY cell consists of:
     *     Relay command           [1 byte]
     *     'Recognized'            [2 bytes]
     *     StreamID                [2 bytes]
     *     Digest                  [4 bytes]
     *     Length                  [2 bytes]
     *     Data                    [CELL_LEN-14 bytes]
     */
	
	 public RelayCell(CircuitNode node, int circuit, int stream, int relayCommand) {
		 this(node, circuit, stream, relayCommand, false);
	 }
	 
	 public RelayCell(CircuitNode node, int circuit, int stream, int relayCommand, boolean isRelayEarly) {
		super(circuit, (isRelayEarly) ? (RELAY_EARLY) : (RELAY));
		this.circuitNode = node;
		this.relayCommand = relayCommand;
		this.streamId = stream;
		this.isOutgoing = true;
		putByte(relayCommand);	// Command
		putShort(0);			// 'Recognized'
		putShort(stream);		// Stream
		putInt(0);				// Digest
		putShort(0);			// Length	
	}

	private RelayCell(CircuitNode node, byte[] rawCell) {
		super(rawCell);
		this.circuitNode = node;
		this.relayCommand = getByte();
		getShort();
		this.streamId = getShort();
		this.isOutgoing = false;
		getInt();
		int payloadLength = getShort();
		cellBuffer.mark(); // End of header
		if(RelayCell.HEADER_SIZE + payloadLength > rawCell.length)
			throw new TorException("Header length field exceeds total size of cell");
		cellBuffer.limit(RelayCell.HEADER_SIZE + payloadLength);
	}

	public int getStreamId() {
		return streamId;
	}

	public int getRelayCommand() {
		return relayCommand;
	}

	public void setLength() {
		putShortAt(LENGTH_OFFSET, (short) (cellBytesConsumed() - HEADER_SIZE));
	}

	public void setDigest(byte[] digest) {
		for(int i = 0; i < 4; i++)
			putByteAt(DIGEST_OFFSET + i, digest[i]);
	}

	public ByteBuffer getPayloadBuffer() {
		final ByteBuffer dup = cellBuffer.duplicate();
		dup.reset();
		return dup.slice();
	}

	public CircuitNode getCircuitNode() {
		return circuitNode;
	}

	public String toString() {
		if(isOutgoing)
			return "["+ commandToDescription(relayCommand) +" stream="+ streamId +" payload_len="+ (cellBytesConsumed() - HEADER_SIZE) +" dest="+ circuitNode +"]";
		else
			return "["+ commandToString() + " stream="+ streamId + " payload_len="+ cellBuffer.remaining() +" source="+ circuitNode + "]";
	}

	public String commandToString() {
		if(relayCommand == RELAY_TRUNCATED) {
			final int code = getByteAt(HEADER_SIZE);
			return commandToDescription(relayCommand) + " ("+ errorToDescription(code) +")";
		} else if(relayCommand == RELAY_END) {
			final int code = getByteAt(HEADER_SIZE);
			return commandToDescription(relayCommand) +" ("+ reasonToDescription(code) +")";
		}
		else
			return commandToDescription(relayCommand);
	}

	public static String reasonToDescription(int reasonCode) {
		switch(reasonCode) {
		case REASON_MISC:
			return "Unlisted reason";
		case REASON_RESOLVEFAILED:
			return "Couldn't look up hostname";
		case REASON_CONNECTREFUSED:
			return "Remote host refused connection";
		case REASON_EXITPOLICY:
			return "OR refuses to connect to host or port";
		case REASON_DESTROY:
			return "Circuit is being destroyed";
		case REASON_DONE:
			return "Anonymized TCP connection was closed";
		case REASON_TIMEOUT:
			return "Connection timed out, or OR timed out while connecting";
		case REASON_HIBERNATING:
			return "OR is temporarily hibernating";
		case REASON_INTERNAL:
			return "Internal error at the OR";
		case REASON_RESOURCELIMIT:
			return "OR has no resources to fulfill request";
		case REASON_CONNRESET:
			return "Connection was unexpectedly reset";
		case REASON_TORPROTOCOL:
			return "Tor protocol violation";
		case REASON_NOTDIRECTORY:
			return "Client sent RELAY_BEGIN_DIR to a non-directory server.";
		default:
			return "Reason code "+ reasonCode;
		}
	}

	public static String commandToDescription(int command) {
		switch(command) {
		case RELAY_BEGIN:
			return "RELAY_BEGIN";
		case RELAY_DATA:
			return "RELAY_DATA";
		case RELAY_END:
			return "RELAY_END";
		case RELAY_CONNECTED:
			return "RELAY_CONNECTED";
		case RELAY_SENDME:
			return "RELAY_SENDME";
		case RELAY_EXTEND:
			return "RELAY_EXTEND";
		case RELAY_EXTENDED:
			return "RELAY_EXTENDED";
		case RELAY_TRUNCATE:
			return "RELAY_TRUNCATE";
		case RELAY_TRUNCATED:
			return "RELAY_TRUNCATED";
		case RELAY_DROP:
			return "RELAY_DROP";
		case RELAY_RESOLVE:
			return "RELAY_RESOLVE";
		case RELAY_RESOLVED:
			return "RELAY_RESOLVED";
		case RELAY_BEGIN_DIR:
			return "RELAY_BEGIN_DIR";
		case RELAY_EXTEND2:
			return "RELAY_EXTEND2";
		case RELAY_EXTENDED2:
			return "RELAY_EXTENDED2";
		default:
			return "Relay command = "+ command;
		}
	}
}
