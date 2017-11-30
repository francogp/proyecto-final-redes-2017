package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.pgas.DistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.DoubleDistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.LongDistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.SimpleMiddleware;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.SimpleProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.File;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

/**
 * This program runs the bubble sort algorithm in a distributed array. This program must be executed in different
 * processes/threads configured by a
 * JSON config file (for more details see {@link SimpleProcessesConfigurations} implementation). The first process listed in
 * the config file will be
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
    private final DistributedArray< I > distributedArray;
    private final Middleware            middleware;
    private String result = null;

    /**
     * @param debugMode true to show debug logs.
     */
    DistributedBubbleSort(
            final DistributedArray< I > distributedArray,
            final Middleware middleware,
            final boolean debugMode
    ) {
        this.distributedArray = distributedArray;
        this.middleware = middleware;
        middleware.startServer();
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
    Runnable getRunnableBubbleSort(
            final int pid,
            final ProcessesConfigurations processesConfigurations,
            final boolean debugMode
    ) {
        final Runnable   bubbleSort;
        final Middleware middleware = new SimpleMiddleware(pid, processesConfigurations, 8);
        switch ( processesConfigurations.getPgasDataType() ) {
            case "Long": {
                final DistributedArray< Long > distributedArray = new LongDistributedArray(PGAS_NAME, middleware);
                bubbleSort = new DistributedBubbleSort<>(distributedArray, middleware, debugMode);
                break;
            }
            case "Double":
                final DistributedArray< Double > distributedArray = new DoubleDistributedArray(PGAS_NAME, middleware);
                bubbleSort = new DistributedBubbleSort<>(distributedArray, middleware, debugMode);
                break;
            default:
                throw new IllegalArgumentException("unknown dataType implementation");
        }
        return bubbleSort;
    }

    public static
    void main( final String... args ) {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PID);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.addValidFlag(ARG_DEBUG_MODE);

        arguments.loadArguments(args);
        final int                     pid                     = arguments.parseInteger(ARG_PID);
        final File                    configFile              = new File(arguments.parseString(ARG_CONFIG_FILE));
        final ProcessesConfigurations processesConfigurations = SimpleProcessesConfigurations.parseFromFile(configFile);
        final boolean                 debugMode               = arguments.existsFlag(ARG_DEBUG_MODE);
        final Runnable                bubbleSort              = getRunnableBubbleSort(pid, processesConfigurations, debugMode);

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
