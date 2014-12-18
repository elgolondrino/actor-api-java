package im.actor.torlib.circuits.hs;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import im.actor.torlib.circuits.*;
import im.actor.torlib.circuits.actors.InternalCircuitMutate;
import im.actor.torlib.crypto.TorTapKeyAgreement;
import im.actor.torlib.directory.NewDirectory;
import im.actor.torlib.directory.routers.Router;
import im.actor.torlib.errors.TorException;

public class RendezvousCircuitBuilder implements Callable<Circuit> {
    private final Logger logger = Logger.getLogger(RendezvousCircuitBuilder.class.getName());

    private final NewDirectory directory;

    private final CircuitManager circuitManager;
    private final HiddenService hiddenService;
    private final HSDescriptor serviceDescriptor;

    public RendezvousCircuitBuilder(NewDirectory directory, CircuitManager circuitManager, HiddenService hiddenService, HSDescriptor descriptor) {
        this.directory = directory;
        this.circuitManager = circuitManager;
        this.hiddenService = hiddenService;
        this.serviceDescriptor = descriptor;
    }

    public Circuit call() throws Exception {

        logger.fine("Opening rendezvous circuit for " + logServiceName());

        final Circuit rendezvous = circuitManager.pickInternalCircuit();
        logger.fine("Establishing rendezvous for " + logServiceName());
        RendezvousProcessor rp = new RendezvousProcessor(rendezvous);
        if (!rp.establishRendezvous()) {
            rendezvous.markForClose();
            return null;
        }
        logger.fine("Opening introduction circuit for " + logServiceName());
        final IntroductionProcessor introductionProcessor = openIntroduction();
        if (introductionProcessor == null) {
            logger.info("Failed to open connection to any introduction point");
            rendezvous.markForClose();
            return null;
        }
        logger.fine("Sending introduce cell for " + logServiceName());
        final TorTapKeyAgreement kex = new TorTapKeyAgreement();
        final boolean icResult = introductionProcessor.sendIntroduce(introductionProcessor.getServiceKey(), kex.getPublicKeyBytes(), rp.getCookie(), rp.getRendezvousRouter());
        introductionProcessor.markCircuitForClose();
        if (!icResult) {
            rendezvous.markForClose();
            return null;
        }
        logger.fine("Processing RV2 for " + logServiceName());
        Circuit hsc = rp.processRendezvous2(kex);
        if (hsc == null) {
            rendezvous.markForClose();
        }

        logger.fine("Rendezvous circuit opened for " + logServiceName());

        return hsc;
    }

    private String logServiceName() {
        return hiddenService.getOnionAddressForLogging();
    }

    private IntroductionProcessor openIntroduction() {
        for (IntroductionPoint ip : serviceDescriptor.getShuffledIntroductionPoints()) {
            final Circuit circuit = attemptOpenIntroductionCircuit(ip);
            if (circuit != null) {
                return new IntroductionProcessor(hiddenService, circuit, ip);
            }
        }
        return null;
    }

    private Circuit attemptOpenIntroductionCircuit(IntroductionPoint ip) {
        final Router r = directory.getRouterByIdentity(ip.getIdentity());
        if (r == null) {
            return null;
        }

        try {
            final Circuit circuit = circuitManager.pickInternalCircuit();
            InternalCircuitMutate.cannibalizeToIntroductionPoint(circuit, r);
            return circuit;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (TorException e) {
            logger.fine("cannibalizeTo() failed : " + e.getMessage());
            return null;
        }
    }
}
