package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.utils.MsgQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.Message.MSG_TYPE_END;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class Server
        implements Runnable {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    final         DatagramPacket      packet;
    private final MsgQueue< Message > msgQueue;
    private final Thread              msgQueueThread;
    private final DatagramSocket      socket;
    private boolean running = false;

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
        byte[] buf = new byte[computeWorstMsgBufferSize()];
        packet = new DatagramPacket(buf, buf.length);

        msgQueue = new MsgQueue<>(processMessageFunction);
        msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
    }

    public static
    int computeWorstMsgBufferSize() {
        final List< String > messages =
                List.of("S:" + Long.MAX_VALUE + ':' + Long.MAX_VALUE, "S:" + Long.MIN_VALUE + ':' + Long.MIN_VALUE, "S:" + 0 + ':' + 0, MSG_TYPE_END);
        return messages.stream().map(msg -> msg.getBytes(DEFAULT_CHARSET).length).max(Integer::compareTo).get();
    }

    private static
    Boolean processPGAS( final Message msg ) {
        System.out.println("Server: " + msg);
        return !msg.isEndMessage();
    }

    public
    DatagramSocket getSocket() {
        return socket;
    }

    public
    boolean isRunning() {
        return running;
    }

    public
    void run() {
        running = true;
        while ( running ) {
            try {
                socket.receive(packet);
                final Message received =
                        new Message(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength(), DEFAULT_CHARSET));

                if ( received.isEndMessage() ) {
                    running = false;
                } else {
                    msgQueue.enqueue(received);
                }
            } catch ( final IOException e ) {
                getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        socket.close();
        msgQueue.enqueue(Message.newEndMessage());
        try {
            msgQueueThread.join();
        } catch ( final InterruptedException e ) {
            getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
