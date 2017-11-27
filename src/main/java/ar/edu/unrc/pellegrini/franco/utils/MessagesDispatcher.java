package ar.edu.unrc.pellegrini.franco.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class MessagesDispatcher< M >
        implements Runnable {

    private final Function< M, Boolean > isFinalMsgFunction;
    private final Consumer< M >          messageConsumer;
    private final BlockingQueue< M >     queue;

    /**
     * @param messageConsumer    what to do with a message.
     * @param isFinalMsgFunction true if the message represent a stop of this dispatcher.
     */
    public
    MessagesDispatcher(
            final Consumer< M > messageConsumer,
            final Function< M, Boolean > isFinalMsgFunction
    ) {
        this.messageConsumer = messageConsumer;
        this.isFinalMsgFunction = isFinalMsgFunction;
        queue = new LinkedBlockingQueue<>();
    }

    public
    void enqueue( final M msg ) {
        queue.add(msg);
    }

    @Override
    public
    void run() {
        try {
            boolean running = true;
            while ( running ) {
                final M message = queue.take();
                if ( isFinalMsgFunction.apply(message) ) {
                    running = false;
                } else {
                    messageConsumer.accept(message);
                }
            }
        } catch ( final InterruptedException ignored ) {
            //ignored
        } catch ( final Exception e ) {
            getLogger(MessagesDispatcher.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
