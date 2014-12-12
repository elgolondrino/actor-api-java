package im.actor.torlib.socks;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import im.actor.torlib.circuits.TorStream;

public class SocksStreamConnection {
	private final static Logger logger = Logger.getLogger(SocksStreamConnection.class.getName());
	
	public static void runConnection(Socket socket, TorStream torStream) {
		SocksStreamConnection ssc = new SocksStreamConnection(socket, torStream);
		ssc.run();
	}
	private final static int TRANSFER_BUFFER_SIZE = 4096;
	private final TorStream torStream;
	private final InputStream torInputStream;
	private final OutputStream torOutputStream;
	private final Socket socket;
	private final Thread incomingThread;
	private final Thread outgoingThread;
	private final Object lock = new Object();
	private volatile boolean outgoingClosed;
	private volatile boolean incomingClosed;

	private SocksStreamConnection(Socket socket, TorStream torStream) {
		this.socket = socket;
		this.torStream = torStream;
		torInputStream = torStream.getInputStream();
		torOutputStream = torStream.getOutputStream();
		
		incomingThread = createIncomingThread();
		outgoingThread = createOutgoingThread();
	}

	private void run() {
		incomingThread.start();
		outgoingThread.start();
		synchronized(lock) {
			while(!(outgoingClosed && incomingClosed)) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				logger.warning("IOException on SOCKS socket close(): "+ e.getMessage());
			}
			closeStream(torInputStream);
			closeStream(torOutputStream);
		}
	}

	private Thread createIncomingThread() {
		return new Thread(new Runnable() { public void run() {
			try {
				incomingTransferLoop();
			} catch (IOException e) {
				logger.fine("System error on incoming stream IO  "+ torStream +" : "+ e.getMessage());
			} finally {
				synchronized(lock) {
					incomingClosed = true;
					lock.notifyAll();
				}
			}
		}});
	}

	private Thread createOutgoingThread() {
		return new Thread(new Runnable() { public void run() {
			try {
				outgoingTransferLoop();
			} catch (IOException e) {
				logger.fine("System error on outgoing stream IO "+ torStream +" : "+ e.getMessage());
			} finally {
				synchronized(lock) {
					outgoingClosed = true;
					lock.notifyAll();
				}
			}
		}});
	}

	private void incomingTransferLoop() throws IOException {
		final byte[] incomingBuffer = new byte[TRANSFER_BUFFER_SIZE];
		while(true) {
			final int n = torInputStream.read(incomingBuffer);
			if(n == -1) {
				logger.fine("EOF on TOR input stream "+ torStream);
				socket.shutdownOutput();
				return;
			} else if(n > 0) {
				logger.fine("Transferring "+ n +" bytes from "+ torStream +" to SOCKS socket");
				if(!socket.isOutputShutdown()) {
					socket.getOutputStream().write(incomingBuffer, 0, n);
					socket.getOutputStream().flush();
				} else {
					closeStream(torInputStream);
					return;
				}
			}
		}
	}

	private void outgoingTransferLoop() throws IOException {
		final byte[] outgoingBuffer = new byte[TRANSFER_BUFFER_SIZE];
		while(true) {
			torStream.waitForSendWindow();
			final int n = socket.getInputStream().read(outgoingBuffer);
			if(n == -1) {
				torOutputStream.close();
				logger.fine("EOF on SOCKS socket connected to "+ torStream);
				return;
			} else if(n > 0) {
				logger.fine("Transferring "+ n +" bytes from SOCKS socket to "+ torStream);
				torOutputStream.write(outgoingBuffer, 0, n);
				torOutputStream.flush();
			}
		}
	}

	private void closeStream(Closeable c) {
		try {
			c.close();
		} catch (IOException e) {
			logger.warning("Close failed on "+ c + " : "+ e.getMessage());
		}	
	}
}
