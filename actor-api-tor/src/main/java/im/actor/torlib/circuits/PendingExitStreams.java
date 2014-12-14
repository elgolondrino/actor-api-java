package im.actor.torlib.circuits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.errors.StreamConnectFailedException;
import im.actor.torlib.TorConfig;
import im.actor.utils.IPv4Address;

public class PendingExitStreams {
	
	private final Set<StreamExitRequest> pendingRequests;
	private final Object lock = new Object();
	private final TorConfig config;

	public PendingExitStreams(TorConfig config) {
		this.config = config;
		pendingRequests = new HashSet<StreamExitRequest>();
	}
	
	public TorStream openExitStream(IPv4Address address, int port) throws InterruptedException, OpenFailedException {
		final StreamExitRequest request = new StreamExitRequest(lock, address, port);
		return openExitStreamByRequest(request);
	}
	
	public TorStream openExitStream(String hostname, int port) throws InterruptedException, OpenFailedException {
		final StreamExitRequest request =  new StreamExitRequest(lock, hostname, port);
		return openExitStreamByRequest(request);
	}
	
	private TorStream openExitStreamByRequest(StreamExitRequest request) throws InterruptedException, OpenFailedException {
		if(config.getCircuitStreamTimeout() != 0) {
			request.setStreamTimeout(config.getCircuitStreamTimeout());
		}
		
		synchronized(lock) {
			pendingRequests.add(request);
			try {
				return handleRequest(request);
			} finally {
				pendingRequests.remove(request);
			}
		}
	}
	
	private TorStream handleRequest(StreamExitRequest request) throws InterruptedException, OpenFailedException {
		while(true) {
			while(!request.isCompleted()) {
				lock.wait();
			}
			try {
				return request.getTorStream();
			} catch (TimeoutException e) {
				request.resetForRetry();
			} catch (StreamConnectFailedException e) {
				request.resetForRetry();
			}
		}
	}

	public List<StreamExitRequest> getUnreservedPendingRequests() {
		final List<StreamExitRequest> result = new ArrayList<StreamExitRequest>();
		synchronized (lock) {
			for(StreamExitRequest request: pendingRequests) {
				if(!request.isReserved()) {
					result.add(request);
				}
			}
		}
		return result;
	}
}
