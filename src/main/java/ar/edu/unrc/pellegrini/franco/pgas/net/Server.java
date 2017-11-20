package ar.edu.unrc.pellegrini.franco.pgas.net;

import ar.edu.unrc.pellegrini.franco.utils.MsgQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.pgas.net.Message.MSG_BYTES_LENGHT;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class Server
        implements Runnable {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final MsgQueue< Message > msgQueue;
    private final Thread              msgQueueThread;
    private final DatagramSocket      socket;
    private boolean running = false;

    public
    Server( final int port )
            throws SocketException {
        this(port, Server::messageConsumer, Server::isQueueFinalizationMsg);
    }

    public
    Server(
            final int port,
            final Consumer< Message > messageConsumer,
            final Function< Message, Boolean > isQueueFinalizationMsg
    )
            throws SocketException {
        socket = new DatagramSocket(port);
        msgQueue = new MsgQueue<>(messageConsumer, isQueueFinalizationMsg);
        msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
    }

    private static
    Boolean isQueueFinalizationMsg( Message message ) {
        return message.isEndMessage();
    }

    private static
    void messageConsumer( final Message msg ) {
        System.out.println("Server: " + msg);
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
                byte[]               buf    = new byte[MSG_BYTES_LENGHT];
                final DatagramPacket packet = new DatagramPacket(buf, MSG_BYTES_LENGHT);
                socket.receive(packet);
                final Message received = new Message(packet.getAddress(), packet.getPort(), packet.getData());
                msgQueue.enqueue(received);
                if ( received.isEndMessage() ) {
                    running = false;
                }
            } catch ( final IOException e ) {
                getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        socket.close();
        try {
            msgQueueThread.join();
        } catch ( final InterruptedException e ) {
            getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public synchronized
    void send(
            final Message msg
    )
            throws IOException {
        final DatagramPacket packet = new DatagramPacket(msg.getBytes(), MSG_BYTES_LENGHT, msg.getAddress(), msg.getPort());
        socket.send(packet);
    }
}
