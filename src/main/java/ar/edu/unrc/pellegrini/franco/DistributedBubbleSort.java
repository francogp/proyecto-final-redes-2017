package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.IntegerPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

public final
class DistributedBubbleSort {

    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_PID         = "pid";
    private static Configs configFile;
    private static int     pid;

    private
    DistributedBubbleSort() {}

    /**
     * BubbleSort clásico
     *
     * @param integerPGAS sobre el cual aplicar el algoritmo
     * @param lowerIndex  el índice mas bajo del rango de valores a ordenar
     * @param upperIndex  el índice mas alto del rango de valores a ordenar
     */
    public static
    void bubbleSort(
            final PGAS< Integer > integerPGAS,
            final long lowerIndex,
            final long upperIndex
    ) {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( int j = 0; j < i; j++ ) {
                if ( integerPGAS.read(j) > integerPGAS.read(j + 1) ) {
                    swapped = true;
                    integerPGAS.swap(j, j + 1);
                }
            }
        }
    }

    private static
    void init( final String... args ) {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PID);
        arguments.addValidArg(ARG_CONFIG_FILE);

        arguments.loadArguments(args);
        pid = arguments.parseInteger(ARG_PID);
        configFile = new Configs(arguments.parseString(ARG_CONFIG_FILE));
    }

    public static
    void main( final String... args ) {
        init(args);
        final PGAS< Integer > integerPGAS = new IntegerPGAS(pid, configFile);
        boolean               finish      = false;

        while ( !finish ) {
            finish = true;
            final long upperIndex = integerPGAS.upperIndex();
            final long lowerIndex = integerPGAS.lowerIndex();

            // sort local block
            bubbleSort(integerPGAS, lowerIndex, upperIndex);
            integerPGAS.barrier();

            if ( !integerPGAS.imLast() ) {
                final long lowerIndexRight = integerPGAS.lowerIndex(pid + 1);
                if ( integerPGAS.read(upperIndex) > integerPGAS.read(lowerIndexRight) ) {
                    integerPGAS.swap(upperIndex, lowerIndexRight);
                    finish = false;  // update local copy
                }
            }
            // reduce finish by and, then replicate result
            finish = integerPGAS.andReduce(finish);
        }
    }
}
