package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.LongPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

public final
class DistributedBubbleSort
        implements Runnable {

    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_PID         = "pid";
    private Configs< Long > configFile;
    private int             pid;

    public
    DistributedBubbleSort(
            final int pid,
            final Configs< Long > configFile
    ) {
        this.pid = pid;
        this.configFile = configFile;
    }

    /**
     * BubbleSort clásico
     *
     * @param longPGAS sobre el cual aplicar el algoritmo
     * @param lowerIndex  el índice mas bajo del rango de valores a ordenar
     * @param upperIndex  el índice mas alto del rango de valores a ordenar
     */
    public static
    void bubbleSort(
            final PGAS< Long > longPGAS,
            final long lowerIndex,
            final long upperIndex
    ) {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( int j = 0; j < i; j++ ) {
                if ( longPGAS.read(j) > longPGAS.read(j + 1) ) {
                    swapped = true;
                    longPGAS.swap(j, j + 1);
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
        int             pid        = arguments.parseInteger(ARG_PID);
        Configs< Long > configFile = new Configs<>(arguments.parseString(ARG_CONFIG_FILE));

        DistributedBubbleSort bubbleSort = new DistributedBubbleSort(pid, configFile);
        bubbleSort.run();
    }

    @Override
    public
    void run() {
        final PGAS< Long > longPGAS = new LongPGAS(pid, configFile);
        boolean            finish   = false;

        while ( !finish ) {
            finish = true;
            final long upperIndex = longPGAS.upperIndex();
            final long lowerIndex = longPGAS.lowerIndex();

            // sort local block
            bubbleSort(longPGAS, lowerIndex, upperIndex);
            longPGAS.barrier();

            if ( !longPGAS.imLast() ) {
                final long lowerIndexRight = longPGAS.lowerIndex(pid + 1);
                if ( longPGAS.read(upperIndex) > longPGAS.read(lowerIndexRight) ) {
                    longPGAS.swap(upperIndex, lowerIndexRight);
                    finish = false;  // update local copy
                }
            }
            // reduce finish by and, then replicate result
            finish = longPGAS.andReduce(finish);
        }
    }
}
