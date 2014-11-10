package im.actor.proto.mtp;

import im.actor.proto.api.ActorApiConfig;
import im.actor.proto.mtp._internal.EndpointProvider;
import im.actor.proto.mtp._internal.entity.ProtoMessage;
import im.actor.proto.mtp._internal.entity.ProtoPackage;
import im.actor.proto.mtp._internal.entity.ProtoSerializer;
import im.actor.proto.mtp._internal.entity.ProtoStruct;
import im.actor.proto.mtp._internal.entity.message.RequestAuthId;
import im.actor.proto.mtp._internal.entity.message.ResponseAuthId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.zip.CRC32;

import javax.net.ssl.SSLSocketFactory;

import static im.actor.proto.util.StreamingUtils.*;

public class AuthIdRetreiver {

    private static final String TAG = "AuthIdRetriever";

    public static void requestAuthId(final ActorApiConfig config, final AuthIdCallback callback) {

        new Thread() {
            @Override
            public void run() {
                try {
                    final MTProtoEndpoint connectionInfo = new EndpointProvider(config.getEndpoints()).fetchEndpoint();
                    Socket socket = SSLSocketFactory.getDefault().createSocket();
                    socket.connect(new InetSocketAddress(connectionInfo.getHost(), connectionInfo.getPort()), 15000);

                    if (!config.isChromeEnabled()) {
                        // This methods crash VM on chrome
                        socket.setKeepAlive(true);
                        socket.setTcpNoDelay(true);
                    }

                    RequestAuthId requestAuthId = new RequestAuthId();
                    ProtoPackage authReqPackage = new ProtoPackage(0, 0, new ProtoMessage(0, requestAuthId.toByteArray()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(authReqPackage.getLength());
                    authReqPackage.writeObject(baos);
                    final byte[] data = baos.toByteArray();

                    final OutputStream outputStream = socket.getOutputStream();
                    final int length = data.length + 8;
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    writeInt(length, os);
                    writeInt(0, os);
                    writeBytes(data, os);
                    CRC32 crc32 = new CRC32();
                    crc32.update(os.toByteArray());
                    writeInt((int) crc32.getValue(), os);

                    writeBytes(os.toByteArray(), outputStream);
                    outputStream.flush();


                    final InputStream inputStream = socket.getInputStream();
                    final int pkgLen = readInt(inputStream);
                    readInt(inputStream);
                    final byte[] pkg = readBytes(pkgLen - 8, inputStream);
                    readInt(inputStream);

                    final ProtoPackage protoPackage = new ProtoPackage(new ByteArrayInputStream(pkg));
                    final ProtoMessage mtpMessage = protoPackage.getPayload();
                    ProtoStruct payload = ProtoSerializer.readMessagePayload(mtpMessage.payload);
                    if (payload != null) {
                        if (payload instanceof ResponseAuthId) {
                            ResponseAuthId responseAuthId = (ResponseAuthId) payload;
                            callback.onSuccess(responseAuthId.authId);
                        } else {
                            callback.onFailure(new Exception(payload.toString()));
                        }
                    } else {
                        callback.onFailure(new Exception("Got null answer"));
                    }

                    socket.close();

                } catch (IOException e) {
                    callback.onFailure(e);
                    if (config.getLogInterface() != null) {
                        config.getLogInterface().e(TAG, e);
                    }

                }
            }
        }.start();
    }

    private static byte[] readBytes(final int count, final InputStream stream) throws IOException {
        final byte[] res = new byte[count];
        int offset = 0;
        while (offset < count) {
            final int readed = stream.read(res, offset, count - offset);
            Thread.yield();
            if (readed > 0) {
                offset += readed;
            } else if (readed < 0) {
                throw new IOException();
            } else {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    throw new IOException();
                }
            }
        }
        return res;
    }

    public static interface AuthIdCallback {
        void onSuccess(long authId);

        void onFailure(Exception e);
    }
}
