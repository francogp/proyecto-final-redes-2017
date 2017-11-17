package ar.edu.unrc.pellegrini.franco;

public
class BubbleSortPGAS {

    private static int pid;

    public static
    void bubbleSort(
            final DistributedArray< Integer > dArray,
            final long lowerIndex,
            final long upperIndex
    ) {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && i >= lowerIndex; i-- ) {
            swapped = false;
            for ( int j = 0; j < i; j++ ) {
                if ( dArray.get(j) > dArray.get(j + 1) ) {
                    swapped = true;
                    dArray.swap(j, j + 1);
                }
            }
        }
    }

    private static
    void init() {
    }

    public static
    void main( String[] args ) {
        init();
        Middleware< Integer >       middleware = new MyMiddleware<>(pid);
        DistributedArray< Integer > dArray     = new MyDistributedArray<>(middleware);
        boolean                     finish     = false;

        while ( !finish ) {
            finish = true;
            final long upperIndex = dArray.upperIndex(pid);
            final long lowerIndex = dArray.lowerIndex(pid);

            // sort local block
            bubbleSort(dArray, lowerIndex, upperIndex);
            middleware.barrier();

            if ( !dArray.imLast() ) {
                final long lowerIndexRight = dArray.lowerIndex(pid + 1);
                if ( dArray.get(upperIndex) > dArray.get(lowerIndexRight) ) {
                    dArray.swap(upperIndex, lowerIndexRight);
                    finish = false;  // update local copy
                }
            }
            // reduce finish by and, then replicate result
            finish = middleware.andReduce(finish);
        }
    }
}
