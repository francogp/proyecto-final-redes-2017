package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.utils.MsgQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

public
class Server
        extends Thread {

    public static final String MSG_TYPE_END = "end";
    private final DatagramSocket      socket;
    private       byte[]              buf;
    private       MsgQueue< Message > msgQueue;
    private       Thread              msgQueueThread;
    private       boolean             running;

    public
    Server( final int port )
            throws SocketException {
        this(port, Server::processPGAS);
    }

    public
    Server(
            final int port,
            final Function< Message, Boolean > processMessageFunction
    )
            throws SocketException {
        socket = new DatagramSocket(port);
        buf = new byte[computeWorstMsgBufferSize()];

        msgQueue = new MsgQueue<>(processMessageFunction);
        msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
    }

    public static
    int computeWorstMsgBufferSize() {
        List< String > messages =
                List.of("S:" + Long.MAX_VALUE + ":" + Long.MAX_VALUE, "S:" + Long.MIN_VALUE + ":" + Long.MIN_VALUE, "S:" + 0 + ":" + 0, MSG_TYPE_END);
        return messages.stream().map(string -> string.getBytes().length).max(Integer::compareTo).get();
    }

    private static
    Boolean processPGAS( Message msg ) {
        System.out.println("Server: " + msg.toString());
        return !msg.isEndMessage();
    }

    public
    DatagramSocket getSocket() {
        return socket;
    }

    public
    void run() {
        running = true;
        while ( running ) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                Message received = new Message(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength()));

                if ( received.isEndMessage() ) {
                    running = false;
                } else {
                    msgQueue.enqueue(received);
                }
            } catch ( IOException e ) {
                getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        socket.close();
        msgQueue.enqueue(Message.newEndMessage());
        try {
            msgQueueThread.join();
        } catch ( InterruptedException e ) {
            getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
