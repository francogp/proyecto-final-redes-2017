package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.utils.MsgQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.LONG_MSG_BYTES_LENGTH;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractServer< I extends Comparable< I > >
        implements Server< I > {

    private final MsgQueue< Message< I > > msgQueue;
    private final Thread                   msgQueueThread;
    private final DatagramSocket           socket;
    private boolean running = false;

    protected
    AbstractServer(
            final int port,
            final Consumer< Message< I > > messageConsumer
    )
            throws SocketException {
        this(port, messageConsumer, Message::isEndMessage);
    }

    protected
    AbstractServer(
            final int port,
            final Consumer< Message< I > > messageConsumer,
            final Function< Message< I >, Boolean > isFinalMsgFunction
    )
            throws SocketException {
        socket = new DatagramSocket(port);
        msgQueue = new MsgQueue<>(messageConsumer, isFinalMsgFunction);
        msgQueueThread = new Thread(msgQueue);
    }

    public final
    boolean isRunning() {
        return running;
    }

    protected abstract
    DatagramPacket newDatagramPacket();

    protected abstract
    Message< I > newMessage( DatagramPacket packet );

    public final
    void run() {
        try {
            msgQueueThread.start();
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
            getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public final
    void send(
            final Message< I > msg
    )
            throws IOException {
        final DatagramPacket packet = new DatagramPacket(msg.getBytes(), LONG_MSG_BYTES_LENGTH, msg.getAddress(), msg.getPort());
        socket.send(packet);
    }
}
