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
public final
class Server
        implements Runnable {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final MsgQueue< Message > msgQueue;
    private final Thread              msgQueueThread;
    private final DatagramSocket      socket;
    private boolean running = false;

    public
    Server(
            final int port,
            final Consumer< Message > messageConsumer
    )
            throws SocketException {
        this(port, messageConsumer, Server::isQueueFinalizationMsg);
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
    Boolean isQueueFinalizationMsg( final Message message ) {
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
        try {
            running = true;
            while ( running ) {
                final DatagramPacket packet = new DatagramPacket(new byte[MSG_BYTES_LENGHT], MSG_BYTES_LENGHT);
                socket.receive(packet);
                final Message received = new Message(packet.getAddress(), packet.getPort(), packet.getData());
                msgQueue.enqueue(received);
                if ( received.isEndMessage() ) {
                    running = false;
                }
            }
            socket.close();
            msgQueueThread.join();
        } catch ( final InterruptedException ignored ) {
            //ignored
        } catch ( final Exception e ) {
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
