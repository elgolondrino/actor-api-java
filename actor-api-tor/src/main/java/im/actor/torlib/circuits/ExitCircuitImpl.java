package im.actor.torlib.circuits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import im.actor.utils.IPv4Address;
import im.actor.torlib.directory.routers.exitpolicy.ExitTarget;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.StreamConnectFailedException;

public class ExitCircuitImpl extends CircuitImpl implements ExitCircuit {

    private final Set<ExitTarget> failedExitRequests;

    public ExitCircuitImpl(List<Router> path, CircuitManager circuitManager) {
        super(path, circuitManager);
        this.failedExitRequests = new HashSet<ExitTarget>();
    }

    public TorStream openExitStream(IPv4Address address, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        return openExitStream(address.toString(), port, timeout);
    }

    public TorStream openExitStream(String target, int port, long timeout) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        final TorStream torStream = createNewStream();
        try {
            torStream.openExit(target, port, timeout);
            return torStream;
        } catch (Exception e) {
            removeStream(torStream);
            return processStreamOpenException(e);
        }
    }

    public void recordFailedExitTarget(ExitTarget target) {
        synchronized (failedExitRequests) {
            failedExitRequests.add(target);
        }
    }

    public boolean canHandleExitTo(ExitTarget target) {
        synchronized (failedExitRequests) {
            if (failedExitRequests.contains(target)) {
                return false;
            }
        }

        if (isMarkedForClose()) {
            return false;
        }

        if (target.isAddressTarget()) {
            return getLastRouter().exitPolicyAccepts(target.getAddress(), target.getPort());
        } else {
            return getLastRouter().exitPolicyAccepts(target.getPort());
        }
    }

    @Override
    protected String getCircuitTypeLabel() {
        return "Exit";
    }
}
