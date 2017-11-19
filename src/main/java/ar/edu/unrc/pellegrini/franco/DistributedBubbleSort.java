package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

public
class DistributedBubbleSort {

    public static final String ARG_ARRAY_SIZE       = "arraySize";
    public static final String ARG_PID              = "pid";
    public static final String ARG_PROCESS_QUANTITY = "processQuantity";
    private static int distributedArraySize;
    private static int pid;
    private static int processQuantity;

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
    void init( final String[] args ) {
        ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PROCESS_QUANTITY);
        arguments.addValidArg(ARG_ARRAY_SIZE);
        arguments.addValidArg(ARG_PID);

        arguments.loadArguments(args);
        processQuantity = arguments.parseInteger(ARG_PROCESS_QUANTITY);
        distributedArraySize = arguments.parseInteger(ARG_ARRAY_SIZE);
        pid = arguments.parseInteger(ARG_PID);
    }

    public static
    void main( String[] args ) {
        init(args);
        Middleware< Integer >       middleware = new MyMiddleware<>(pid, processQuantity);
        DistributedArray< Integer > dArray     = new MyDistributedArray<>(middleware, distributedArraySize);
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
