package ar.edu.unrc.pellegrini.franco.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public
class Client {
    public static synchronized
    void sendTo(
            final DatagramSocket socket,
            final String destAddress,
            final int destPort,
            final String msg
    )
            throws IOException {
        InetAddress    address = InetAddress.getByName(destAddress);
        byte[]         buf     = msg.getBytes();
        DatagramPacket packet  = new DatagramPacket(buf, buf.length, address, destPort);
        socket.send(packet);
    }

    public static synchronized
    void sendTo(
            final DatagramSocket socket,
            final Message msg
    )
            throws IOException {
        byte[]         buf    = msg.getValue().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, msg.getAddress(), msg.getPort());
        socket.send(packet);
    }
}
