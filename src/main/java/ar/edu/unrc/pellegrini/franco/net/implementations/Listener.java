package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.InvalidValueParameterException;
import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.MessagesDispatcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.AbstractMessage.PAYLOAD_PREFIX_LENGTH;
import static java.util.logging.Logger.getLogger;

/**
 * A listener read UDP packages, parse them into {@link Message} and deliver them to a {@link MessagesDispatcher}.
 *
 * @param <I> value to be carried by the message.
 */
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

    /**
     * @param socket              to listen.
     * @param messageConsumer     implementation of what to do with the message when processed.
     * @param valueByteBufferSize byte size of the value type I.
     * @param newMessageSupplier  a supplier of new {@link Message} instances.
     */
    public
    Listener(
            final DatagramSocket socket,
            final Consumer< Message< I > > messageConsumer,
            final int valueByteBufferSize,
            final Supplier< Message< I > > newMessageSupplier
    ) {
        this.socket = socket;
        payloadLength = PAYLOAD_PREFIX_LENGTH + valueByteBufferSize;
        this.newMessageSupplier = newMessageSupplier;
        messagesDispatcher = new MessagesDispatcher<>(messageConsumer, Message::isEndMessage);
        msgQueueThread = new Thread(messagesDispatcher);
    }

    public
    void run() {
        try {
            msgQueueThread.start();
            running = true;
            while ( running ) {
                final DatagramPacket packet = new DatagramPacket(new byte[payloadLength], payloadLength);
                socket.receive(packet);
                try {
                    final Message< I > received = newMessageSupplier.get();
                    received.initUsing(packet);
                    messagesDispatcher.enqueue(received);
                    if ( received.isEndMessage() ) {
                        running = false;
                    }
                } catch ( InvalidValueParameterException e ) {
                    getLogger(Listener.class.getName()).log(Level.SEVERE, "Cannot parse DatagramPacket: " + packet, e);
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