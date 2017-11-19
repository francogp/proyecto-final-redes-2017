package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

public
class DistributedBubbleSort {

    public static final String ARG_ARRAY_SIZE       = "arraySize";
    public static final String ARG_PID              = "pid";
    public static final String ARG_PROCESS_QUANTITY = "processQuantity";
    private static int DistributedArraySize;
    private static int pid;
    private static int processQuantity;

    public static
    void bubbleSort(
            final DistributedArray< Integer > distArray,
            final long lowerIndex,
            final long upperIndex
    ) {
        boolean swapped = true;
        for ( long i = upperIndex; swapped && i >= lowerIndex; i-- ) {
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
    void init( final String[] args ) {
        ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PROCESS_QUANTITY);
        arguments.addValidArg(ARG_ARRAY_SIZE);
        arguments.addValidArg(ARG_PID);

        arguments.loadArguments(args);
        processQuantity = arguments.parseInteger(ARG_PROCESS_QUANTITY);
        DistributedArraySize = arguments.parseInteger(ARG_ARRAY_SIZE);
        pid = arguments.parseInteger(ARG_PID);
    }

    public static
    void main( String[] args ) {
        init(args);
        Middleware< Integer >       middleware = new MyMiddleware<>(pid, processQuantity);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, DistributedArraySize);
        boolean                     finish     = false;

        while ( !finish ) {
            finish = true;
            final long upperIndex = distArray.upperIndex(pid);
            final long lowerIndex = distArray.lowerIndex(pid);

            // sort local block
            bubbleSort(distArray, lowerIndex, upperIndex);
            middleware.barrier();

            if ( !distArray.imLast() ) {
                final long lowerIndexRight = distArray.lowerIndex(pid + 1);
                if ( distArray.get(upperIndex) > distArray.get(lowerIndexRight) ) {
                    distArray.swap(upperIndex, lowerIndexRight);
                    finish = false;  // update local copy
                }
            }
            // reduce finish by and, then replicate result
            finish = middleware.andReduce(finish);
        }
    }
}
