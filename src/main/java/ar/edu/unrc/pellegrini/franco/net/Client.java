package ar.edu.unrc.pellegrini.franco.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static ar.edu.unrc.pellegrini.franco.net.Server.DEFAULT_CHARSET;

public final
class Client {
    private
    Client() {}

    public static synchronized
    void sendTo(
            final DatagramSocket socket,
            final String destAddress,
            final int destPort,
            final String msg
    )
            throws IOException {
        final InetAddress    address = InetAddress.getByName(destAddress);
        final byte[]         buf     = msg.getBytes(DEFAULT_CHARSET);
        final DatagramPacket packet  = new DatagramPacket(buf, buf.length, address, destPort);
        socket.send(packet);
    }

    public static synchronized
    void sendTo(
            final DatagramSocket socket,
            final Message msg
    )
            throws IOException {
        final byte[]         buf    = msg.getValue().getBytes(DEFAULT_CHARSET);
        final DatagramPacket packet = new DatagramPacket(buf, buf.length, msg.getAddress(), msg.getPort());
        socket.send(packet);
    }
}
