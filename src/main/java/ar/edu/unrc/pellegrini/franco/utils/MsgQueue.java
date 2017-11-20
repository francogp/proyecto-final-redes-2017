package ar.edu.unrc.pellegrini.franco.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class MsgQueue< M >
        implements Runnable {

    private final Function< M, Boolean >   processMessageFunction;
    private final LinkedBlockingQueue< M > queue;
    private boolean running = false;

    /**
     * @param processMessageFunction M = message to process, and return true if must continue to process further messages.
     */
    public
    MsgQueue(
            final Function< M, Boolean > processMessageFunction
    ) {
        this.processMessageFunction = processMessageFunction;
        queue = new LinkedBlockingQueue<>();
    }

    public
    void enqueue( final M msg ) {
        queue.add(msg);
    }

    public
    boolean isRunning() {
        return running;
    }

    @SuppressWarnings( "ErrorNotRethrown" )
    @Override
    public
    void run() {
        try {
            running = true;
            while ( running ) {
                final M message = queue.take();
                running = processMessageFunction.apply(message);
            }
        } catch ( final InterruptedException ignored ) {
            //no hacer nada si se interrumpe esperando
        } catch ( final Error e ) {
            getLogger(MsgQueue.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
