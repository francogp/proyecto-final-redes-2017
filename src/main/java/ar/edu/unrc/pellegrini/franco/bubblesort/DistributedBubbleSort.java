package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;

import java.io.IOException;

public
interface DistributedBubbleSort< I extends Comparable< I > >
        extends Runnable {

    String ARG_CONFIG_FILE = "configFile";
    String ARG_DEBUG_MODE  = "debug";
    String ARG_PID         = "pid";

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

    String result();
}
