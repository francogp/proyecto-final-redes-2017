package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.distributedapi.DistributedArray;
import ar.edu.unrc.pellegrini.franco.distributedapi.IntegerDistributedArray;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

public final
class DistributedBubbleSort {

    public static final String ARG_CONFIG_FILE = "configFile";
    public static final String ARG_PID         = "pid";
    private static Configs configFile;
    private static long    distributedArraySize;
    private static int     pid;
    private static int     processQuantity;

    private
    DistributedBubbleSort() {}

    /**
     * BubbleSort clásico
     *
     * @param distArray  sobre el cual aplicar el algoritmo
     * @param lowerIndex el índice mas bajo del rango de valores a ordenar
     * @param upperIndex el índice mas alto del rango de valores a ordenar
     */
    public static
    void bubbleSort(
            final DistributedArray< Integer > distArray,
            final long lowerIndex,
            final long upperIndex
    ) {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && ( i >= lowerIndex ); i-- ) {
            swapped = false;
            for ( int j = 0; j < i; j++ ) {
                if ( distArray.get(j) > distArray.get(j + 1) ) {
                    swapped = true;
                    distArray.swap(j, j + 1);
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
        final DistributedArray< Integer > distArray = new IntegerDistributedArray(pid, configFile);
        boolean                           finish    = false;

        while ( !finish ) {
            finish = true;
            final long upperIndex = distArray.upperIndex();
            final long lowerIndex = distArray.lowerIndex();

            // sort local block
            bubbleSort(distArray, lowerIndex, upperIndex);
            distArray.barrier();

            if ( !distArray.imLast() ) {
                final long lowerIndexRight = distArray.lowerIndex(pid + 1);
                if ( distArray.get(upperIndex) > distArray.get(lowerIndexRight) ) {
                    distArray.swap(upperIndex, lowerIndexRight);
                    finish = false;  // update local copy
                }
            }
            // reduce finish by and, then replicate result
            finish = distArray.andReduce(finish);
        }
    }
}
