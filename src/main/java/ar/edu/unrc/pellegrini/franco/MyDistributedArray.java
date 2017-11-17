package ar.edu.unrc.pellegrini.franco;

import java.util.ArrayList;
import java.util.List;

public
class MyDistributedArray< I >
        implements DistributedArray< I > {

    private final List< Indexes > indexes;
    private final Middleware< I > middleware;
    private final int             pid;
    private final long            realSize;

    public
    MyDistributedArray(
            final Middleware< I > middleware,
            final long realSize
    ) {
        this.middleware = middleware;
        this.pid = middleware.getPid();
        this.realSize = realSize;
        final int processQuantity = middleware.getProcessQuantity();
        long      actualSize      = realSize / processQuantity;
        if ( actualSize > Integer.MAX_VALUE ) {
            throw new IllegalArgumentException("middleware.getProcessQuantity() is too small");
        }
        indexes = new ArrayList<>(processQuantity);
        long lastLowerIndex = 0;
        for ( int i = 0; i < processQuantity - 1; i++ ) {
            long lowerIndex = lastLowerIndex;
            long upperIndex = lowerIndex + actualSize - 1;
            indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
            lastLowerIndex = upperIndex + 1;
        }
        long lowerIndex = lastLowerIndex;
        actualSize = realSize - ( actualSize * ( processQuantity - 1 ) ) - 1;
        long upperIndex = lowerIndex + actualSize;
        indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
    }

    @Override
    public
    I get( final long index ) {
        return null;
    }

    @Override
    public
    boolean imLast() {
        return middleware.imLast();
    }

    @Override
    public
    long lowerIndex( final int pid ) {
        return indexes.get(pid - 1).getLoweIndex();
    }

    @Override
    public
    void set(
            final long index,
            final I value
    ) {

    }

    @Override
    public
    void swap(
            long index1,
            long index2
    ) {

    }

    @Override
    public
    long upperIndex( final int pid ) {
        return indexes.get(pid - 1).getUpperIndex();
    }

    private
    class Indexes {
        private long loweIndex;
        private int  size;
        private long upperIndex;

        public
        Indexes(
                final long loweIndex,
                final long upperIndex,
                final int size
        ) {
            this.loweIndex = loweIndex;
            this.upperIndex = upperIndex;
            this.size = size;
        }

        public
        long getLoweIndex() {
            return loweIndex;
        }

        public
        int getSize() {
            return size;
        }

        public
        long getUpperIndex() {
            return upperIndex;
        }
    }
}
