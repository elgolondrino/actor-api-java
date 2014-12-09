package im.actor.api.mtp._internal.tcp;

import java.io.*;
import java.net.Socket;

/**
 * Created by ex3ndr on 10.12.14.
 */
public class SocksProxy {

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static Socket createProxiedSocket(String proxyHost, int proxyPort, String destHost, int destPort) throws IOException {
        Socket socket = new Socket(proxyHost, proxyPort);
        OutputStream out = socket.getOutputStream();

        // CONNECT request
        ByteArrayOutputStream request = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(request);
        dataOutputStream.writeByte(0x04);// SOCKS version
        dataOutputStream.writeByte(0x01);// establish a TCP/IP stream connection
        // Destination port
        dataOutputStream.writeByte((0xFF00 & destPort) >> 8);
        dataOutputStream.writeByte(0xFF & destPort);
        if (destHost.matches(IPADDRESS_PATTERN)) {
            String[] parts = destHost.split("\\.");
            dataOutputStream.writeByte(Integer.parseInt(parts[0]));
            dataOutputStream.writeByte(Integer.parseInt(parts[1]));
            dataOutputStream.writeByte(Integer.parseInt(parts[2]));
            dataOutputStream.writeByte(Integer.parseInt(parts[3]));
            // Empty UserID
            dataOutputStream.writeByte(0);
        } else {
            dataOutputStream.writeByte(0);
            dataOutputStream.writeByte(0);
            dataOutputStream.writeByte(0);
            dataOutputStream.writeByte(1);
            // Empty UserID
            dataOutputStream.writeByte(0);
            // Destination Host name
            dataOutputStream.writeBytes(destHost);
            dataOutputStream.writeByte(0);
        }
        dataOutputStream.flush();

        out.write(request.toByteArray());

        // CONNECT response
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        if (inputStream.readByte() != 0) {
            throw new IOException();
        }
        int resp = inputStream.readByte();
        if (resp != 0x5a) {
            throw new IOException();
        }
        // Port
        inputStream.readByte();
        inputStream.readByte();
        // IP
        inputStream.readByte();
        inputStream.readByte();
        inputStream.readByte();
        inputStream.readByte();
        return socket;
    }
}
