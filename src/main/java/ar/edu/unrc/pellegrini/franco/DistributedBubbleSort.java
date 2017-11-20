package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.LongPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

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
            final Configs< Long > configFile
    ) {
        longPGAS = new LongPGAS(pid, configFile);
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
            throws IOException {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( long j = 0L; j < i; j++ ) {
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
        final int             pid        = arguments.parseInteger(ARG_PID);
        final Configs< Long > configFile = new Configs<>(arguments.parseString(ARG_CONFIG_FILE));

        final DistributedBubbleSort bubbleSort = new DistributedBubbleSort(pid, configFile);
        bubbleSort.run();
    }

    @Override
    public
    void run() {
        try {
            boolean finish = false;

            while ( !finish ) {
                finish = true;
                final long upperIndex = longPGAS.upperIndex();
                final long lowerIndex = longPGAS.lowerIndex();

                // sort local block
                bubbleSort(longPGAS, lowerIndex, upperIndex);

                longPGAS.barrier();

                if ( !longPGAS.imLast() ) {
                    final long lowerIndexRight = longPGAS.lowerIndex(longPGAS.getPid() + 1);
                    if ( longPGAS.read(upperIndex) > longPGAS.read(lowerIndexRight) ) {
                        longPGAS.swap(upperIndex, lowerIndexRight);
                        finish = false;  // update local copy
                    }
                }
                // reduce finish by and, then replicate result
                finish = longPGAS.andReduce(finish);
            }
        } catch ( final Exception e ) {
            getLogger(DistributedBubbleSort.class.getName()).log(Level.SEVERE, "Unknown problem", e);
        }
    }
}
