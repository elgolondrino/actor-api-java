package im.actor.torlib.circuits.actors;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.concurrency.FutureCallback;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.streams.TorStream;
import im.actor.utils.IPv4Address;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ex3ndr on 15.12.14.
 */
public class StreamСreateActor extends TaskActor<TorStream> {

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
        return ActorSystem.system().actorOf(Props.create(StreamСreateActor.class, new ActorCreator<StreamСreateActor>() {
            @Override
            public StreamСreateActor create() {
                return new StreamСreateActor(address, manager);
            }
        }), "/tor/streams/" + NEXT_ID.getAndIncrement() + "/create");
    }

    private DestinationAddress address;
    private CircuitManager manager;

    public StreamСreateActor(DestinationAddress address, CircuitManager manager) {
        this.address = address;
        this.manager = manager;
    }

    @Override
    public void startTask() {
        if (address instanceof IpDestinationAddress) {
            ask(manager.openExitStreamTo(((IpDestinationAddress) address).getAddress(), address.getPort()),
                    new FutureCallback<TorStream>() {
                        @Override
                        public void onResult(TorStream result) {
                            complete(result);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            error(throwable);
                        }
                    });
        } else if (address instanceof HiddenServiceAddress) {
            ask(manager.openExitStreamTo(((HiddenServiceAddress) address).getAddress() + ".onion", address.getPort()),
                    new FutureCallback<TorStream>() {
                        @Override
                        public void onResult(TorStream result) {
                            complete(result);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            error(throwable);
                        }
                    });
        } else if (address instanceof HostAddress) {
            ask(manager.openExitStreamTo(((HostAddress) address).getHost(), address.getPort()),
                    new FutureCallback<TorStream>() {
                        @Override
                        public void onResult(TorStream result) {
                            complete(result);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            error(throwable);
                        }
                    });
        } else if (address instanceof DirectoryAddress) {
            error(new Exception("Directory address is unsupported"));
        } else {
            error(new Exception("Unknown destination address"));
        }
    }
}
