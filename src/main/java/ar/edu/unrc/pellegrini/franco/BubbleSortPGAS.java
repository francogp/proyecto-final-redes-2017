package ar.edu.unrc.pellegrini.franco;

public
class BubbleSortPGAS {

    private static int pid;

    private static
    void bubbleSort(
            final long lowerIndex,
            final long upperIndex
    ) {

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
            bubbleSort(lowerIndex, upperIndex);
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
