package im.actor.torlib.circuits.actors;

import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.actors.target.ExitCircuitStreamRequest;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.torlib.errors.StreamConnectFailedException;

public class ExitCircuitStreamTask implements Runnable {
    private final static Logger logger = Logger.getLogger(ExitCircuitStreamTask.class.getName());
    private final Circuit circuit;
    private final ExitCircuitStreamRequest exitRequest;

    public ExitCircuitStreamTask(Circuit circuit, ExitCircuitStreamRequest exitRequest) {
        this.circuit = circuit;
        this.exitRequest = exitRequest;
    }

    public void run() {
        logger.fine("Attempting to open stream to " + exitRequest);
        try {
            exitRequest.complete(tryOpenExitStream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exitRequest.error(e);
        } catch (TimeoutException e) {
            circuit.markForClose();
            exitRequest.error(e);
        } catch (StreamConnectFailedException e) {
            if (!e.isReasonRetryable()) {
                exitRequest.error(e);
                // circuit.recordFailedExitTarget(exitRequest);
            } else {
                circuit.markForClose();
                exitRequest.error(e);
            }
        }
    }

    // TODO: Fix timeouts
    private TorStream tryOpenExitStream() throws InterruptedException, TimeoutException, StreamConnectFailedException {
        if (exitRequest.isAddressTarget()) {
            return CircuitStreamFactory.openExitStream(circuit, exitRequest.getAddress(), exitRequest.getPort(), 15000);
        } else {
            return CircuitStreamFactory.openExitStream(circuit, exitRequest.getHostname(), exitRequest.getPort(), 15000);
        }
    }

}
