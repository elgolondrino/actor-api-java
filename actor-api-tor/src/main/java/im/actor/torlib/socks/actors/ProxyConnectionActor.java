package im.actor.torlib.socks.actors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import im.actor.torlib.circuits.streams.TorStream;

public class ProxyConnectionActor extends Actor {

    public static void runConnection(int connectionId, final Socket socket, final TorStream torStream) {
        ActorSystem.system().actorOf(Props.create(ProxyConnectionActor.class, new ActorCreator<ProxyConnectionActor>() {
            @Override
            public ProxyConnectionActor create() {
                return new ProxyConnectionActor(socket, torStream);
            }
        }), "/tor/proxy/" + connectionId + "/run");
    }

    private final static Logger LOG = Logger.getLogger(ProxyConnectionActor.class.getName());

    private final static int TRANSFER_BUFFER_SIZE = 4096;

    private TorStream torStream;
    private Socket socket;

    private boolean isRunning = true;

    private InputStream torInputStream;
    private OutputStream socketOutputStream;

    private OutputStream torOutputStream;
    private InputStream socketInputStream;

    private Thread incomingThread;
    private Thread outgoingThread;

    private ProxyConnectionActor(Socket socket, TorStream torStream) {
        this.socket = socket;
        this.torStream = torStream;
    }

    @Override
    public void preStart() {
        try {
            socketOutputStream = socket.getOutputStream();
            socketInputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            self().send(new Stop());
            return;
        }

        torInputStream = torStream.getInputStream();
        torOutputStream = torStream.getOutputStream();

        incomingThread = createIncomingThread();
        outgoingThread = createOutgoingThread();

        incomingThread.start();
        outgoingThread.start();
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Stop) {
            stop();
        }
    }

    private void stop() {
        isRunning = false;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (torStream != null) {
                torStream.close();
                torStream = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread createIncomingThread() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    final byte[] incomingBuffer = new byte[TRANSFER_BUFFER_SIZE];
                    while (isRunning) {
                        final int n = torInputStream.read(incomingBuffer);
                        if (n == -1) {
                            socket.shutdownOutput();
                            return;
                        } else if (n > 0) {
                            if (!socket.isOutputShutdown()) {
                                socketOutputStream.write(incomingBuffer, 0, n);
                                socketOutputStream.flush();
                            } else {
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    self().send(new Stop());
                }
            }
        });
    }

    private Thread createOutgoingThread() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    final byte[] outgoingBuffer = new byte[TRANSFER_BUFFER_SIZE];
                    while (true) {
                        torStream.waitForSendWindow();
                        final int n = socketInputStream.read(outgoingBuffer);
                        if (n == -1) {
                            LOG.fine("EOF on SOCKS socket connected to " + torStream);
                            return;
                        } else if (n > 0) {
                            LOG.fine("Transferring " + n + " bytes from SOCKS socket to " + torStream);
                            torOutputStream.write(outgoingBuffer, 0, n);
                            torOutputStream.flush();
                        }
                    }
                } catch (Exception e) {
                    // LOG.fine("System error on outgoing stream IO " + torStream + " : " + e.getMessage());
                } finally {
                    self().send(new Stop());
                }
            }
        });
    }

    private static final class Stop {

    }
}
