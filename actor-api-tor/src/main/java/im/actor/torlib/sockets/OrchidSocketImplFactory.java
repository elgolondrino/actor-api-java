package im.actor.torlib.sockets;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

import im.actor.torlib.TorClient;
import im.actor.torlib.TorClient;

public class OrchidSocketImplFactory implements SocketImplFactory {
	private final TorClient torClient;
	
	public OrchidSocketImplFactory(TorClient torClient) {
		this.torClient = torClient;
	}

	public SocketImpl createSocketImpl() {
		return new OrchidSocketImpl(torClient);
	}
}
