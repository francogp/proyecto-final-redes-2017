package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.MessagesDispatcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.implementations.SimpleMessage.PAYLOAD_PREFIX_LENGTH;
import static java.util.logging.Logger.getLogger;

/**
 * A listener read UDP packages, parse them into {@link Message} and deliver them to a {@link MessagesDispatcher}.
 */
@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class Listener
        implements Runnable {

    private final MessagesDispatcher< Message > messagesDispatcher;
    private final Thread                        msgQueueThread;
    private final int                           payloadLength;
    private final DatagramSocket                socket;

    /**
     * @param socket                 to listen.
     * @param messageConsumer        implementation of what to do with the message when processed.
     * @param maxValueByteBufferSize maximum byte size of an object value to be transported in a message.
     */
    public
    Listener(
            final DatagramSocket socket,
            final Consumer< Message > messageConsumer,
            final int maxValueByteBufferSize
    ) {
        this.socket = socket;
        payloadLength = PAYLOAD_PREFIX_LENGTH + maxValueByteBufferSize;
        messagesDispatcher = new MessagesDispatcher<>(messageConsumer, Message::isEndMessage);
        msgQueueThread = new Thread(messagesDispatcher);
    }

    public
    void run() {
        try {
            msgQueueThread.start();
            boolean running = true;
            while ( running ) {
                final DatagramPacket packet = new DatagramPacket(new byte[payloadLength], payloadLength);
                socket.receive(packet);
                final Message received = new SimpleMessage();
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
