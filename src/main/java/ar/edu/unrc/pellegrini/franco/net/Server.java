package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessageServer;
import ar.edu.unrc.pellegrini.franco.utils.MsgQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.MSG_BYTES_LENGTH;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class Server< I extends Comparable< I > >
        implements Runnable {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final MsgQueue< Message< I > > msgQueue;
    private final Thread                   msgQueueThread;
    private final DatagramSocket           socket;
    private boolean running = false;

    protected
    Server(
            final int port,
            final Consumer< Message< I > > messageConsumer
    )
            throws SocketException {
        this(port, messageConsumer, Message::isEndMessage);
    }

    protected
    Server(
            final int port,
            final Consumer< Message< I > > messageConsumer,
            final Function< Message< I >, Boolean > isQueueFinalizationMsg
    )
            throws SocketException {
        socket = new DatagramSocket(port);
        msgQueue = new MsgQueue<>(messageConsumer, isQueueFinalizationMsg);
        msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
    }

    public
    DatagramSocket getSocket() {
        return socket;
    }

    public
    boolean isRunning() {
        return running;
    }

    protected abstract
    DatagramPacket newDatagramPacket();

    protected abstract
    Message< I > newMessage( DatagramPacket packet );

    public
    void run() {
        try {
            running = true;
            while ( running ) {
                final DatagramPacket packet = newDatagramPacket();
                socket.receive(packet);
                final Message< I > received = newMessage(packet);
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
            getLogger(LongMessageServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public
    void send(
            final Message< I > msg
    )
            throws IOException {
        final DatagramPacket packet = new DatagramPacket(msg.getBytes(), MSG_BYTES_LENGTH, msg.getAddress(), msg.getPort());
        socket.send(packet);
    }
}
