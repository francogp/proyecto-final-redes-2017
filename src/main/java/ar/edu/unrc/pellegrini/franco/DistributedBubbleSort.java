package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.LongPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.IOException;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedBubbleSort
        implements Runnable {

    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_PID         = "pid";
    private final PGAS< Long > longPGAS;

    public
    DistributedBubbleSort(
            final int pid,
            final String configFilePath
    ) {
        longPGAS = new LongPGAS(pid, configFilePath);
    }

    /**
     * BubbleSort clásico
     *
     * @param longPGAS   sobre el cual aplicar el algoritmo
     * @param lowerIndex el índice mas bajo del rango de valores a ordenar
     * @param upperIndex el índice mas alto del rango de valores a ordenar
     */
    public static
    void bubbleSort(
            final PGAS< Long > longPGAS,
            final long lowerIndex,
            final long upperIndex
    )
            throws IOException, InterruptedException {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( long j = lowerIndex; j < i; j++ ) {
                if ( longPGAS.read(j) > longPGAS.read(j + 1L) ) {
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

        arguments.loadArguments(args);
        final int                   pid        = arguments.parseInteger(ARG_PID);
        final DistributedBubbleSort bubbleSort = new DistributedBubbleSort(pid, arguments.parseString(ARG_CONFIG_FILE));
        bubbleSort.run();
    }

    @Override
    public
    void run() {
        try {
            System.out.println("Flag 1 " + Thread.currentThread().getName());
            boolean finish = false;

            while ( !finish ) {
                finish = true;
                System.out.println("Flag 2 " + Thread.currentThread().getName());
                final long upperIndex = longPGAS.upperIndex();
                final long lowerIndex = longPGAS.lowerIndex();

                System.out.println("Flag 3 " + Thread.currentThread().getName());
                // sort local block
                bubbleSort(longPGAS, lowerIndex, upperIndex);

                System.out.println("Flag 4 " + Thread.currentThread().getName());
                longPGAS.barrier();

                System.out.println("Flag 5 " + Thread.currentThread().getName());
                if ( !longPGAS.imLast() ) {
                    final long lowerIndexRight = longPGAS.lowerIndex(longPGAS.getPid() + 1);
                    if ( longPGAS.read(upperIndex) > longPGAS.read(lowerIndexRight) ) {
                        longPGAS.swap(upperIndex, lowerIndexRight);
                        finish = false;  // update local copy
                    }
                }
                //TODO cleanup debug code
                //                System.out.println("Flag 6 " + Thread.currentThread().getName());
                // reduce finish by and, then replicate result
                finish = longPGAS.andReduce(finish);
            }
            System.out.println("Flag 7 " + Thread.currentThread().getName());
            if ( longPGAS.isCoordinator() ) {
                System.out.println(longPGAS.asString());
            }
        } catch ( final Exception e ) {
            getLogger(DistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
