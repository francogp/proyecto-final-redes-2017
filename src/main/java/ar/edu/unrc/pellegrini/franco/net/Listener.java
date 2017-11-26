package ar.edu.unrc.pellegrini.franco.net;

import ar.edu.unrc.pellegrini.franco.utils.MessagesDispatcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.AbstractMessage.PAYLOAD_PREFIX_LENGTH;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Listener< I >
        implements Runnable {

    private final MessagesDispatcher< Message< I > > messagesDispatcher;
    private final Thread                             msgQueueThread;
    private final Supplier< Message< I > >           newMessageSupplier;
    private final int                                payloadLength;
    private final DatagramSocket                     socket;
    private boolean running = false;

    public
    Listener(
            final DatagramSocket socket,
            final Consumer< Message< I > > messageConsumer,
            final Function< Message< I >, Boolean > isFinalMsgFunction,
            final int valueByteBufferSize,
            final Supplier< Message< I > > newMessageSupplier
    ) {
        this.socket = socket;
        payloadLength = PAYLOAD_PREFIX_LENGTH + valueByteBufferSize;
        this.newMessageSupplier = newMessageSupplier;
        messagesDispatcher = new MessagesDispatcher<>(messageConsumer, isFinalMsgFunction);
        msgQueueThread = new Thread(messagesDispatcher);
    }

    public
    boolean isRunning() {
        return running;
    }

    public
    void run() {
        try {
            msgQueueThread.start();
            running = true;
            while ( running ) {
                final DatagramPacket packet = new DatagramPacket(new byte[payloadLength], payloadLength);
                socket.receive(packet);
                final Message< I > received = newMessageSupplier.get();
                received.initUsing(packet);
                messagesDispatcher.enqueue(received);
                if ( received.isEndMessage() ) {
                    running = false;
                }
            }
            socket.close();
            msgQueueThread.join();
        } catch ( final InterruptedException ignored ) {
            //ignored
        } catch ( final Exception e ) {
            getLogger(Listener.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
