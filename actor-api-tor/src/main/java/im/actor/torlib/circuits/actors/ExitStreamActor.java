package im.actor.torlib.circuits.actors;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.TorStream;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.utils.IPv4Address;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class ExitStreamActor extends TaskActor<TorStream> {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static ActorRef openExitStream(String host, int port, CircuitManager manager) {
        if (host.endsWith(".onion")) {
            return openExitStream(new HiddenServiceAddress(host.substring(0, host.indexOf(".onion")), port), manager);
        } else {
            return openExitStream(new HostAddress(host, port), manager);
        }
    }

    public static ActorRef openExitStream(IPv4Address address, int port, CircuitManager manager) {
        return openExitStream(new IpDestinationAddress(address, port), manager);
    }

    public static ActorRef openExitStream(final DestinationAddress address, final CircuitManager manager) {
        return ActorSystem.system().actorOf(Props.create(ExitStreamActor.class, new ActorCreator<ExitStreamActor>() {
            @Override
            public ExitStreamActor create() {
                return new ExitStreamActor(address, manager);
            }
        }), "/tor/streams/" + NEXT_ID.getAndIncrement() + "/create");
    }

    private DestinationAddress address;
    private CircuitManager manager;

    public ExitStreamActor(DestinationAddress address, CircuitManager manager) {
        this.address = address;
        this.manager = manager;
    }

    @Override
    public void startTask() {
        try {
            if (address instanceof IpDestinationAddress) {
                complete(manager.openExitStreamTo(((IpDestinationAddress) address).getAddress(), address.getPort()));
            } else if (address instanceof HiddenServiceAddress) {
                complete(manager.openExitStreamTo(((HiddenServiceAddress) address).getAddress() + ".onion", address.getPort()));
            } else if (address instanceof HostAddress) {
                complete(manager.openExitStreamTo(((HostAddress) address).getHost(), address.getPort()));
            } else {
                error(new Exception("Unknown destination address"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            error(e);
        } catch (TimeoutException e) {
            e.printStackTrace();
            error(e);
        } catch (OpenFailedException e) {
            e.printStackTrace();
            error(e);
        }
    }
}
