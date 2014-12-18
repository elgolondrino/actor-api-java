package im.actor.torlib.circuits.build;

import im.actor.torlib.circuits.Circuit;
import im.actor.torlib.circuits.streams.CircuitNode;
import im.actor.torlib.circuits.build.extender.CircuitExtender;
import im.actor.torlib.directory.routers.Router;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class InternalCircuitMutate {

    public static void cannibalizeToDirectory(Circuit circuit, Router target) {
        final CircuitExtender extender = new CircuitExtender(circuit);
        extender.extendTo(target);
    }

    public static void cannibalizeToIntroductionPoint(Circuit circuit, Router target) {
        final CircuitExtender extender = new CircuitExtender(circuit);
        extender.extendTo(target);
    }

    public static void connectHiddenService(Circuit circuit, CircuitNode node) {
        circuit.appendNode(node);
    }
}
