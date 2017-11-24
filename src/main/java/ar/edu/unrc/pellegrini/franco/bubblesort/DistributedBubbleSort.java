package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.pgas.DistributedArray;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class DistributedBubbleSort< I extends Comparable< I > >
        implements Runnable {
    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_DEBUG_MODE  = "debug";
    public static final String ARG_PID         = "pid";
    private final Middleware< I > middleware;
    private final PGAS< I >       pgas;
    private       String          result;

    protected
    DistributedBubbleSort(
            final int pid,
            final String configFilePath,
            final Supplier< Message< I > > newMessageSupplier,
            final int valueByteBufferSize,
            final boolean debugMode
    ) {
        this(pid, new NetConfiguration< I >(configFilePath), newMessageSupplier, valueByteBufferSize, debugMode);
    }

    protected
    DistributedBubbleSort(
            final int pid,
            final NetConfiguration< I > configFile,
            final Supplier< Message< I > > newMessageSupplier,
            final int valueByteBufferSize,
            final boolean debugMode
    ) {
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = new Middleware<>(pid, configFile, newMessageSupplier, valueByteBufferSize);
        middleware.startServer();
        pgas = new DistributedArray< I >(1, configFile, middleware);
        pgas.setDebugMode(debugMode);
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
            throws IOException, InterruptedException {
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
        //        arguments.addValidArg();
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.addValidFlag(ARG_DEBUG_MODE);

        arguments.loadArguments(args);
        final int pid = arguments.parseInteger(ARG_PID);
        final Runnable bubbleSort =
                new DistributedBubbleSort(pid, arguments.parseString(ARG_CONFIG_FILE), () -> LongMessage.getInstance(), 8, //TODO parametrizar
                        arguments.existsFlag(ARG_DEBUG_MODE));
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
                final long upperIndex = pgas.upperIndex();
                final long lowerIndex = pgas.lowerIndex();

                // sort local block
                bubbleSort(pgas, lowerIndex, upperIndex);

                middleware.barrier();

                if ( !middleware.imLast() ) {
                    final long lowerIndexRight = pgas.lowerIndex(middleware.getPid() + 1);
                    if ( pgas.read(upperIndex).compareTo(pgas.read(lowerIndexRight)) > 0 ) {
                        pgas.swap(upperIndex, lowerIndexRight);
                        finish = false;  // update local copy
                    }
                }
                // reduce finish by and, then replicate result
                finish = middleware.andReduce(finish);
            }

            if ( middleware.isCoordinator() ) {
                result = pgas.asString();
            }

            middleware.endService();
        } catch ( final Exception e ) {
            getLogger(DistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
