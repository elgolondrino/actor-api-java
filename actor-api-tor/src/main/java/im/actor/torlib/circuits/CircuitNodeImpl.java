package im.actor.torlib.circuits;

import im.actor.torlib.circuits.cells.Cell;
import im.actor.torlib.circuits.cells.RelayCell;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.TorException;

public class CircuitNodeImpl {
	
	public static CircuitNodeImpl createAnonymous(CircuitNodeImpl previous, byte[] keyMaterial, byte[] verifyDigest) {
		return createNode(null, previous, keyMaterial, verifyDigest);
	}
	
	public static CircuitNodeImpl createFirstHop(Router r, byte[] keyMaterial, byte[] verifyDigest) {
		return createNode(r, null, keyMaterial, verifyDigest);
	}
	
	public static CircuitNodeImpl createNode(Router r, CircuitNodeImpl previous, byte[] keyMaterial, byte[] verifyDigest) {
		final CircuitNodeCryptoState cs = CircuitNodeCryptoState.createFromKeyMaterial(keyMaterial, verifyDigest);
		return new CircuitNodeImpl(r, previous, cs);
	}

	private final static int CIRCWINDOW_START = 1000;
	private final static int CIRCWINDOW_INCREMENT = 100;

	private final Router router;
	private final CircuitNodeCryptoState cryptoState;
	private final CircuitNodeImpl previousNode;

	private final Object windowLock;
	private int packageWindow;
	private int deliverWindow;
	
	private CircuitNodeImpl(Router router, CircuitNodeImpl previous, CircuitNodeCryptoState cryptoState) {
		previousNode = previous;
		this.router = router;
		this.cryptoState = cryptoState;
		windowLock = new Object();
		packageWindow = CIRCWINDOW_START;
		deliverWindow = CIRCWINDOW_START;
	}

	public Router getRouter() {
		return router;
	}

	public CircuitNodeImpl getPreviousNode() {
		return previousNode;
	}

	public void encryptForwardCell(RelayCell cell) {
		cryptoState.encryptForwardCell(cell);
	}

	public boolean decryptBackwardCell(Cell cell) {
		return cryptoState.decryptBackwardCell(cell);
	}

	public void updateForwardDigest(RelayCell cell) {
		cryptoState.updateForwardDigest(cell);
	}

	public byte[] getForwardDigestBytes() {
		return cryptoState.getForwardDigestBytes();
	}

	public String toString() {
		if(router != null) {
			return "|"+ router.getNickname() + "|";
		} else {
			return "|()|";
		}
	}

	public void decrementDeliverWindow() {
		synchronized(windowLock) {
			deliverWindow--;
		}
	}

	public boolean considerSendingSendme() {
		synchronized(windowLock) {
			if(deliverWindow <= (CIRCWINDOW_START - CIRCWINDOW_INCREMENT)) {
				deliverWindow += CIRCWINDOW_INCREMENT;
				return true;
			}
			return false;
		}
	}

	public void waitForSendWindow() {
		waitForSendWindow(false);
	}

	public void waitForSendWindowAndDecrement() {
		waitForSendWindow(true);
	}

	private void waitForSendWindow(boolean decrement) {
		synchronized(windowLock) {
			while(packageWindow == 0) {
				try {
					windowLock.wait();
				} catch (InterruptedException e) {
					throw new TorException("Thread interrupted while waiting for circuit send window");
				}
			}
			if(decrement)
				packageWindow--;
		}
	}

	public void incrementSendWindow() {
		synchronized(windowLock) {
			packageWindow += CIRCWINDOW_INCREMENT;
			windowLock.notifyAll();
		}
		
	}
}
