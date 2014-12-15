package im.actor.torlib.circuits;

import java.util.List;
import java.util.concurrent.TimeoutException;

import im.actor.torlib.circuits.build.extender.CircuitExtender;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.StreamConnectFailedException;

public class InternalCircuitImpl extends CircuitImpl implements InternalCircuit, DirectoryCircuit, HiddenServiceCircuit {

    private enum InternalType {UNUSED, HS_INTRODUCTION, HS_DIRECTORY, HS_CIRCUIT}

    private InternalType type;

    public InternalCircuitImpl(List<Router> path, CircuitManager circuitManager) {
        super(path, circuitManager);
        this.type = InternalType.UNUSED;
    }

    public Circuit cannibalizeToIntroductionPoint(Router target) {
        cannibalizeTo(target);
        type = InternalType.HS_INTRODUCTION;
        return this;
    }

    private void cannibalizeTo(Router target) {
        if (type != InternalType.UNUSED) {
            throw new IllegalStateException("Cannot cannibalize internal circuit with type " + type);

        }
        final CircuitExtender extender = new CircuitExtender(this);
        extender.extendTo(target);
    }

    public TorStream openDirectoryStream(long timeout, boolean autoclose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
        if (type != InternalType.HS_DIRECTORY) {
            throw new IllegalStateException("Cannot open directory stream on internal circuit with type " + type);
        }
        final TorStream torStream = createNewStream();
        try {
            torStream.openDirectory(timeout);
            return torStream;
        } catch (Exception e) {
            removeStream(torStream);
            return processStreamOpenException(e);
        }
    }


    public DirectoryCircuit cannibalizeToDirectory(Router target) {
        cannibalizeTo(target);
        type = InternalType.HS_DIRECTORY;
        return this;
    }


    public HiddenServiceCircuit connectHiddenService(CircuitNode node) {
        if (type != InternalType.UNUSED) {
            throw new IllegalStateException("Cannot connect hidden service from internal circuit type " + type);
        }
        appendNode(node);
        type = InternalType.HS_CIRCUIT;
        return this;
    }

    public TorStream openStream(int port, long timeout)
            throws InterruptedException, TimeoutException, StreamConnectFailedException {
        if (type != InternalType.HS_CIRCUIT) {
            throw new IllegalStateException("Cannot open stream to hidden service from internal circuit type " + type);
        }
        final TorStream torStream = createNewStream();
        try {
            torStream.openExit("", port, timeout);
            return torStream;
        } catch (Exception e) {
            removeStream(torStream);
            return processStreamOpenException(e);
        }
    }


    @Override
    protected String getCircuitTypeLabel() {
        switch (type) {
            case HS_CIRCUIT:
                return "Hidden Service";
            case HS_DIRECTORY:
                return "HS Directory";
            case HS_INTRODUCTION:
                return "HS Introduction";
            case UNUSED:
                return "Internal";
            default:
                return "(null)";
        }
    }
}
