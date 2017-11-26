package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.pgas.*;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage.DOUBLE_VALUE_PARAMETER_BYTE_SIZE;
import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.LONG_VALUE_PARAMETER_BYTE_SIZE;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class DistributedBubbleSort< I extends Comparable< I > >
        implements Runnable {
    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_DEBUG_MODE  = "debug";
    public static final String ARG_PID         = "pid";
    public static final int    PGAS_NAME       = 99;
    private final PGAS< I >       distributedArray;
    private final Middleware< I > middleware;
    private String result = null;

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
     * @param longPGAS   sobre el cual aplicar el algoritmo
     * @param lowerIndex el índice mas bajo del rango de valores a ordenar
     * @param upperIndex el índice mas alto del rango de valores a ordenar
     */
    static
    < I extends Comparable< I > > void bubbleSort(
            final PGAS< I > longPGAS,
            final long lowerIndex,
            final long upperIndex
    )
            throws Exception {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( long j = lowerIndex; j < i; j++ ) {
                if ( longPGAS.read(j).compareTo(longPGAS.read(j + 1L)) > 0 ) {
                    swapped = true;
                    longPGAS.swap(j, j + 1L);
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

        switch ( processesConfigurations.getPgasDataType() ) {
            case "Long":
                bubbleSort = new DistributedBubbleSort<>(pid,
                        (ProcessesConfigurations< Long >) processesConfigurations,
                        LongMessage::getInstance,
                        LONG_VALUE_PARAMETER_BYTE_SIZE,
                        arguments.existsFlag(ARG_DEBUG_MODE));
                break;
            case "Double":
                bubbleSort = new DistributedBubbleSort<>(pid,
                        (ProcessesConfigurations< Double >) processesConfigurations,
                        DoubleMessage::getInstance,
                        DOUBLE_VALUE_PARAMETER_BYTE_SIZE,
                        arguments.existsFlag(ARG_DEBUG_MODE));
                break;
            default:
                throw new IllegalArgumentException("unknown dataType implementation");
        }
        bubbleSort.run();
    }

    public final
    String result() {
        return result;
    }

    @Override
    public final
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

            if ( middleware.isCoordinator() ) {
                result = distributedArray.asString();
            }

            middleware.closeListener();
        } catch ( final Exception e ) {
            getLogger(DistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
