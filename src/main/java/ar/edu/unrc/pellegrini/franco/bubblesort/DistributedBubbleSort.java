package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.DistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.SimpleMiddleware;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.SimpleProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage.DOUBLE_VALUE_PARAMETER_BYTE_SIZE;
import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.LONG_VALUE_PARAMETER_BYTE_SIZE;
import static java.util.logging.Logger.getLogger;

/**
 * This program runs the bubble sort algorithm in a distributed array. This program must be executed in different processes/threads configured by a
 * JSON config file (for more details see {@link SimpleProcessesConfigurations} implementation). The first process listed in the config file will be
 * considered as the coordinator.
 * <p>
 * It can be used as a thread.
 *
 * @param <I> value to be sorted.
 */
@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedBubbleSort< I extends Comparable< I > >
        implements Runnable {
    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_DEBUG_MODE  = "debug";
    public static final String ARG_PID         = "pid";
    public static final int    PGAS_NAME       = 99;
    private final PGAS< I >       distributedArray;
    private final Middleware< I > middleware;
    private String result = null;

    /**
     * @param pid                     of this process.
     * @param processesConfigurations for all the processes.
     * @param newMessageSupplier      a supplier of new {@link Message} instances.
     * @param valueByteBufferSize     byte size of the value type I.
     * @param debugMode               true to show debug logs.
     */
    protected
    DistributedBubbleSort(
            final int pid,
            final ProcessesConfigurations< I > processesConfigurations,
            final Supplier< Message< I > > newMessageSupplier,
            final int valueByteBufferSize,
            final boolean debugMode
    ) {
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = new SimpleMiddleware<>(pid, processesConfigurations, newMessageSupplier, valueByteBufferSize);
        middleware.startServer();
        distributedArray = new DistributedArray<>(PGAS_NAME, middleware);
        distributedArray.setDebugMode(debugMode);
    }

    /**
     * BubbleSort clásico
     *
     * @param distributedArray to sort.
     * @param lowerIndex       in the range to sort.
     * @param upperIndex       in the range to sort.
     */
    static
    < I extends Comparable< I > > void bubbleSort(
            final PGAS< I > distributedArray,
            final long lowerIndex,
            final long upperIndex
    )
            throws Exception {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( long j = lowerIndex; j < i; j++ ) {
                if ( distributedArray.read(j).compareTo(distributedArray.read(j + 1L)) > 0 ) {
                    swapped = true;
                    distributedArray.swap(j, j + 1L);
                }
            }
        }
    }

    public static
    void main( final String... args ) {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PID);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.addValidFlag(ARG_DEBUG_MODE);

        arguments.loadArguments(args);
        final int                          pid                     = arguments.parseInteger(ARG_PID);
        final Runnable                     bubbleSort;
        final File                         configFile              = new File(arguments.parseString(ARG_CONFIG_FILE));
        final ProcessesConfigurations< ? > processesConfigurations = SimpleProcessesConfigurations.parseFromFile(configFile);
        final boolean                      debugMode               = arguments.existsFlag(ARG_DEBUG_MODE);
        switch ( processesConfigurations.getPgasDataType() ) {
            case "Long":
                bubbleSort = new DistributedBubbleSort<>(pid,
                        (ProcessesConfigurations< Long >) processesConfigurations,
                        LongMessage::getInstance,
                        LONG_VALUE_PARAMETER_BYTE_SIZE,
                        debugMode);
                break;
            case "Double":
                bubbleSort = new DistributedBubbleSort<>(pid,
                        (ProcessesConfigurations< Double >) processesConfigurations,
                        DoubleMessage::getInstance,
                        DOUBLE_VALUE_PARAMETER_BYTE_SIZE,
                        debugMode);
                break;
            default:
                throw new IllegalArgumentException("unknown dataType implementation");
        }
        //Not as a thread, just as a method.
        bubbleSort.run();
    }

    /**
     * @return results of the sorting method
     */
    public
    String result() {
        return result;
    }

    @Override
    public
    void run() {
        try {
            boolean finish = false;

            while ( !finish ) {
                finish = true;
                final long upperIndex = distributedArray.upperIndex();
                final long lowerIndex = distributedArray.lowerIndex();

                // sort local block
                bubbleSort(distributedArray, lowerIndex, upperIndex);

                middleware.barrier();

                if ( !middleware.imLast() ) {
                    final long lowerIndexRight = distributedArray.lowerIndex(middleware.getWhoAmI() + 1);
                    if ( distributedArray.read(upperIndex).compareTo(distributedArray.read(lowerIndexRight)) > 0 ) {
                        distributedArray.swap(upperIndex, lowerIndexRight);
                        finish = false;  // update local copy
                    }
                }
                // reduce finish by and, then replicate result
                finish = middleware.andReduce(finish);
            }

            result = distributedArray.asString();

            middleware.closeListener();
        } catch ( final Exception e ) {
            getLogger(DistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
