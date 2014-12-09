package im.actor.api.mtp._internal.tcp;

import com.droidkit.actors.ActorRef;

import im.actor.api.LogInterface;
import im.actor.api.mtp.MTProtoEndpoint;
import im.actor.api.mtp.MTProtoParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import javax.net.ssl.SSLSocketFactory;

import static im.actor.api.util.StreamingUtils.*;

public class TcpConnection implements RawTcpConnection {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private static final int CONNECTION_TIMEOUT = 5 * 1000;
    private static final int READ_DIE_TIMEOUT = 15 * 1000; // 5 sec
    private static final int MAX_PACKAGE_SIZE = 1024 * 1024;

    private static final AtomicInteger PACKAGE = new AtomicInteger(1);
    private final String TAG;
    private final LogInterface LOG;
    private final boolean DEBUG;
    private final Socket socket;
    private final ReaderThread readerThread;
    private final WriterThread writerThread;
    private final DieThread dieThread;
    private final ActorRef receiver;
    private final int connectionId;
    private final MTProtoParams params;
    private int sentPackets;
    private int receivedPackets;
    private boolean isClosed;
    private boolean isBroken;
    private long lastWriteEvent = 0;

    public TcpConnection(MTProtoEndpoint endpoint, MTProtoParams params, final ActorRef receiver) throws IOException {
        try {
            this.connectionId = NEXT_ID.incrementAndGet();
            this.params = params;
            this.TAG = "TcpConnection#" + connectionId;
            this.LOG = params.getConfig().getLogInterface();
            this.DEBUG = params.getConfig().isDebugTcp();
            if (params.getConfig().getProxy() != null) {
                if (endpoint.getEndpointType() == MTProtoEndpoint.EndpointType.PLAIN_TCP) {
                    this.socket = SocksProxy.createProxiedSocket(params.getConfig().getProxy().getHost(),
                            params.getConfig().getProxy().getPort(),
                            endpoint.getHost(), endpoint.getPort());
                } else {
                    Socket underlying = SocksProxy.createProxiedSocket(params.getConfig().getProxy().getHost(),
                            params.getConfig().getProxy().getPort(),
                            endpoint.getHost(), endpoint.getPort());
                    socket = ((SSLSocketFactory) SSLSocketFactory.getDefault())
                            .createSocket(underlying,
                                    endpoint.getHost(),
                                    endpoint.getPort(),
                                    true);
                }
            } else {
                if (endpoint.getEndpointType() == MTProtoEndpoint.EndpointType.PLAIN_TCP) {
                    this.socket = new Socket();
                } else {
                    this.socket = SSLSocketFactory.getDefault().createSocket();
                }
                this.socket.connect(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()), CONNECTION_TIMEOUT);
            }

            this.socket.getInputStream();
            if (!params.getConfig().isChromeEnabled()) {
                this.socket.setKeepAlive(true);
                this.socket.setTcpNoDelay(true);
            }
            this.isClosed = false;
            this.isBroken = false;
            this.receiver = receiver;
            this.readerThread = new ReaderThread();
            this.writerThread = new WriterThread();
            this.dieThread = new DieThread();
            this.readerThread.start();
            this.writerThread.start();
            this.dieThread.start();
        } catch (final IOException e) {
            throw e;
        } catch (final Throwable t) {
            throw new IOException();
        }
    }

    public int getConnectionId() {
        return connectionId;
    }

    public int getSentPackets() {
        return sentPackets;
    }

    public int getReceivedPackets() {
        return receivedPackets;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void postMessage(final byte[] data) {
        writerThread.pushPackage(new Package(data));
    }

    public synchronized void close() {
        if (!isClosed) {
            if (LOG != null) {
                LOG.w(TAG, "Manual context closing");
            }
            isClosed = true;
            isBroken = false;
            try {
                readerThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }
            try {
                writerThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }

            try {
                dieThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }
        }
    }

    private synchronized void onMessage(final byte[] data, final int len) {
        if (isClosed) {
            if (LOG != null) {
                LOG.w(TAG, "Ignoring package: connection closed");
            }
            return;
        }

        int id = PACKAGE.incrementAndGet();
        if (LOG != null && DEBUG) {
            LOG.d(TAG, "Sending #" + id);
        }
        receiver.send(new RawMessage(id, data, 0, len, connectionId));
    }

    private synchronized void breakConnection() {
        if (!isClosed) {
            if (LOG != null) {
                LOG.w(TAG, "Breaking connection");
            }
            isClosed = true;
            isBroken = true;
            try {
                readerThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }
            try {
                writerThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }

            try {
                dieThread.interrupt();
            } catch (final Exception e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
            }
        }

        receiver.send(new ConnectionDie(this.getConnectionId()));
    }

    private void onWrite() {
        lastWriteEvent = System.currentTimeMillis();
        notifyDieThread();
    }

    private void onRead() {
        lastWriteEvent = 0;
        notifyDieThread();
    }

    private void notifyDieThread() {
        synchronized (dieThread) {
            dieThread.notifyAll();
        }
    }

    private byte[] readBytes(final int count, final InputStream stream) throws IOException {
        final byte[] res = new byte[count];
        int offset = 0;
        while (offset < count) {
            final int readed = stream.read(res, offset, count - offset);
            if (readed > 0) {
                offset += readed;
                onRead();
            } else {
                throw new IOException();
            }
        }
        return res;
    }

    private class Package {
        public byte[] data;

        public Package() {

        }

        private Package(final byte[] data) {
            this.data = data;
        }
    }

    private class ReaderThread extends Thread {
        private ReaderThread() {
            setPriority(Thread.MIN_PRIORITY);
            setName(TAG + "#Reader" + hashCode());
        }

        @Override
        public void run() {
            try {
                while (!isClosed && !isInterrupted()) {
                    try {
                        if (socket.isClosed()) {
                            if (LOG != null) {
                                LOG.w(TAG, "Socket is closed");
                            }
                            breakConnection();
                            return;
                        }
                        if (!socket.isConnected()) {
                            if (LOG != null) {
                                LOG.w(TAG, "Socket is not connected");
                            }
                            breakConnection();
                            return;
                        }

                        InputStream stream = socket.getInputStream();
                        long start = System.currentTimeMillis();

                        // Length
                        if (LOG != null && DEBUG) {
                            LOG.d(TAG, "Reading content length");
                        }
                        int pkgLen = readInt(stream);
                        if (pkgLen < 0 || pkgLen > MAX_PACKAGE_SIZE) {
                            if (LOG != null) {
                                LOG.w(TAG, "Invalid package size: " + pkgLen);
                            }
                            throw new IOException("Invalid package size");
                        }

                        // Index
                        if (LOG != null && DEBUG) {
                            LOG.d(TAG, "Reading package index");
                        }
                        int pkgIndex = readInt(stream);
                        int expectedIndex = receivedPackets++;
                        if (pkgIndex != expectedIndex) {
                            if (LOG != null) {
                                LOG.w(TAG, "Wrong seq. Expected " + expectedIndex + ", got " + pkgIndex);
                            }
                            throw new IOException("Wrong number of received packets");
                        }

                        // Content
                        if (LOG != null && DEBUG) {
                            LOG.d(TAG, "Reading package content of " + pkgLen + " bytes");
                        }
                        byte[] pkg = readBytes(pkgLen - 8, stream);

                        // CRC32
                        if (LOG != null && DEBUG) {
                            LOG.d(TAG, "Reading CRC32");
                        }
                        int pkgCrc = readInt(stream);
                        CRC32 crc32 = new CRC32();
                        crc32.update(intToBytes(pkgLen));
                        crc32.update(intToBytes(pkgIndex));
                        crc32.update(pkg);
                        int localCrc = (int) crc32.getValue();

                        if (localCrc != pkgCrc) {
                            if (LOG != null) {
                                LOG.w(TAG, "Package crc32 expected: " + localCrc + ", got: " + pkgCrc);
                            }
                            throw new IOException("Wrong CRC");
                        }

                        if (LOG != null && DEBUG) {
                            LOG.d(TAG, "Read #" + pkgIndex + " in " + (System.currentTimeMillis() - start) + " ms");
                        }
                        onMessage(pkg, pkgLen);
                    } catch (final IOException e) {
                        if (LOG != null) {
                            LOG.e(TAG, e);
                        }
                        breakConnection();
                        return;
                    }
                }
            } catch (final Throwable e) {
                if (LOG != null) {
                    LOG.e(TAG, e);
                }
                breakConnection();
            }
        }
    }

    private class WriterThread extends Thread {

        private final CRC32 crc32 = new CRC32();
        private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<Package>();

        public WriterThread() {
            setPriority(Thread.MIN_PRIORITY);
            setName(TAG + "#Writer" + hashCode());
        }

        public void pushPackage(final Package p) {
            packages.add(p);
            synchronized (packages) {
                packages.notifyAll();
            }
        }

        @Override
        public void run() {
            while (!isClosed && !isInterrupted()) {
                Package p;
                synchronized (packages) {
                    p = packages.poll();
                    if (p == null) {
                        try {
                            packages.wait();
                        } catch (final InterruptedException e) {
                            return;
                        }
                        p = packages.poll();
                    }
                }
                if (p == null) {
                    if (isBroken) {
                        return;
                    } else {
                        continue;
                    }
                }

                try {
                    byte[] data = p.data;
                    int length = data.length + 8;

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    writeInt(length, outputStream);
                    writeInt(sentPackets, outputStream);
                    writeBytes(data, outputStream);
                    crc32.reset();
                    crc32.update(outputStream.toByteArray());
                    writeInt((int) crc32.getValue(), outputStream);
                    socket.getOutputStream().write(outputStream.toByteArray());
                    socket.getOutputStream().flush();
                    onWrite();

                    sentPackets++;
                } catch (final Exception e) {
                    if (LOG != null) {
                        LOG.e(TAG, e);
                    }
                    breakConnection();
                }
            }
        }
    }

    private class DieThread extends Thread {
        public DieThread() {
            setPriority(Thread.MIN_PRIORITY);
            setName(TAG + "#DieThread" + hashCode());
        }

        @Override
        public void run() {
            while (!isBroken) {
                if (lastWriteEvent != 0) {
                    final long delta = (System.currentTimeMillis() - lastWriteEvent);
                    if (delta >= READ_DIE_TIMEOUT) {
                        if (LOG != null) {
                            LOG.w(TAG, "Dies by timeout");
                        }
                        breakConnection();
                    } else {
                        try {
                            final int waitDelta = (int) (READ_DIE_TIMEOUT - delta);
                            // Logger.d(TAG, "DieThread wait: " + waitDelta);
                            sleep(Math.max(waitDelta, 1000));
                            // Logger.d(TAG, "DieThread start wait end");
                        } catch (final InterruptedException e) {
                            return;
                        }
                    }
                } else {
                    try {
                        // Logger.d(TAG, "DieThread start common wait");
                        sleep(READ_DIE_TIMEOUT);
                        // Logger.d(TAG, "DieThread end common wait");
                    } catch (final InterruptedException e) {
                        return;
                    }
                }
            }
        }
    }

    public static class RawMessage {
        private int id;
        private byte[] data;
        private int offset;
        private int len;
        private int contextId;

        public RawMessage(int id, byte[] data, int offset, int len, int contextId) {
            this.id = id;
            this.data = data;
            this.offset = offset;
            this.len = len;
            this.contextId = contextId;
        }

        public int getId() {
            return id;
        }

        public byte[] getData() {
            return data;
        }

        public int getOffset() {
            return offset;
        }

        public int getLen() {
            return len;
        }

        public int getContextId() {
            return contextId;
        }
    }

    public static class ConnectionDie {
        private int contextId;

        public ConnectionDie(int contextId) {
            this.contextId = contextId;
        }

        public int getContextId() {
            return contextId;
        }
    }
}