package im.actor.proto.mtp._internal.tcp;

/**
 * Created by ex3ndr on 11.11.14.
 */
public interface RawTcpConnection {
    public void close();

    public boolean isClosed();

    public int getConnectionId();

    public void postMessage(final byte[] data);
}
