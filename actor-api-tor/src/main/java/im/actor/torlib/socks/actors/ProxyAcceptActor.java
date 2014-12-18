package im.actor.torlib.socks.actors;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.droidkit.actors.*;
import com.droidkit.actors.tasks.AskCallback;
import im.actor.torlib.circuits.CircuitManager;
import im.actor.torlib.circuits.actors.StreamСreateActor;
import im.actor.torlib.errors.OpenFailedException;
import im.actor.torlib.circuits.streams.TorStream;

public class ProxyAcceptActor extends Actor {

    private static final AtomicInteger NEXT_CONNECTION_ID = new AtomicInteger(1);

    public static void acceptConnection(final Socket socket, final CircuitManager circuitManager) {
        final int id = NEXT_CONNECTION_ID.getAndIncrement();
        ActorSystem.system().actorOf(Props.create(ProxyAcceptActor.class, new ActorCreator<ProxyAcceptActor>() {
            @Override
            public ProxyAcceptActor create() {
                return new ProxyAcceptActor(id, socket, circuitManager);
            }
        }), "/tor/proxy/" + id + "/init");
    }

    private final static Logger LOG = Logger.getLogger(ProxyAcceptActor.class.getName());

    private final int id;
    private final Socket socket;
    private final CircuitManager circuitManager;

    public ProxyAcceptActor(int id, Socket socket, CircuitManager circuitManager) {
        this.id = id;
        this.socket = socket;
        this.circuitManager = circuitManager;
    }

    @Override
    public void preStart() {
        final int version = readByte();
        if (!dispatchRequest(version)) {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.warning("Error closing SOCKS socket: " + e.getMessage());
            }
        }
    }

    private int readByte() {
        try {
            return socket.getInputStream().read();
        } catch (IOException e) {
            LOG.warning("IO error reading version byte: " + e.getMessage());
            return -1;
        }
    }

    private boolean dispatchRequest(int versionByte) {
        switch (versionByte) {
            case 4:
                return processRequest(new Socks4Request(socket));
            case 5:
                return processRequest(new Socks5Request(socket));
            default:
                // fall through, do nothing
                // Closing connection
                return false;
        }
    }

    private boolean processRequest(final SocksRequest request) {
        try {
            request.readRequest();
            if (!request.isConnectRequest()) {
                LOG.warning("Non connect command (" + request.getCommandCode() + ")");
                request.sendError(true);
                return false;
            }

            LOG.info("SOCKS CONNECT to " + request.getTarget() + " starting opening sequence");
            ActorRef askActor;
            if (request.hasHostname()) {
                askActor = StreamСreateActor.openExitStream(request.getHostname(), request.getPort(), circuitManager);
            } else {
                askActor = StreamСreateActor.openExitStream(request.getAddress(), request.getPort(), circuitManager);
            }
            ask(askActor, new AskCallback<TorStream>() {
                @Override
                public void onResult(TorStream stream) {
                    LOG.info("SOCKS CONNECT to " + request.getTarget() + " completed");
                    try {
                        request.sendSuccess();
                    } catch (SocksRequestException e) {
                        e.printStackTrace();
                    }
                    ProxyConnectionActor.runConnection(id, socket, stream);
                }

                @Override
                public void onError(Throwable throwable) {
                    try {
                        LOG.warning("SOCKS CONNECT to " + request.getTarget() + " failed: " + throwable.getMessage());
                        if (throwable instanceof OpenFailedException) {
                            try {
                                request.sendConnectionRefused();
                            } catch (SocksRequestException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                request.sendError(false);
                            } catch (SocksRequestException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return true;
        } catch (SocksRequestException e) {
            LOG.warning("Failure reading SOCKS request: " + e.getMessage());
            try {
                request.sendError(false);
                socket.close();
            } catch (Exception ignore) {
            }
        }
        return false;
    }
}
