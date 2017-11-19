package ar.edu.unrc.pellegrini.franco;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public
class MyDistributedArray< I >
        implements DistributedArray< I > {

    private final I[]             array;
    private final long            currentLowerIndex;
    private final long            currentUpperIndex;
    private final List< Indexes > indexes;
    private final Middleware< I > middleware;
    private final int             pid;
    private final long            realSize;
    private final int             size;

    public
    MyDistributedArray(
            final Class< I > c,
            final Middleware< I > middleware,
            final long realSize
    ) {
        this.middleware = middleware;
        this.pid = middleware.getPid();
        this.realSize = realSize;
        final int processQuantity = middleware.getProcessQuantity();
        //iniciamos el tamaño que debería tener la porción del arreglo correspondiente al proceso distribuido actual
        long actualSize = realSize / processQuantity;
        if ( actualSize > Integer.MAX_VALUE ) {
            throw new IllegalArgumentException("middleware.getProcessQuantity() is too small");
        }
        // inicializamos los indices lowerIndex y upperIndex
        indexes = new ArrayList<>(processQuantity);
        long lowerIndex = 0;
        long upperIndex;
        for ( int i = 0; i < processQuantity - 1; i++ ) {
            upperIndex = lowerIndex + actualSize - 1;
            indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
            lowerIndex = upperIndex + 1;
        }
        // inicializamos lowerIndex y upperIndex para el ultimo caso, donde colocamos el resto, en caso que la division al calcular actualSize no
        // sea equitativa entre todos los procesos distribuidos.
        actualSize = realSize - ( actualSize * ( processQuantity - 1 ) );
        upperIndex = lowerIndex + actualSize - 1;
        indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        // inicializamos el arreglo del proceso actual
        size = indexes.get(pid - 1).getSize();
        array = (I[]) Array.newInstance(c, size);//new Object[size];
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware.setDistArray(this);
    }

    @Override
    public synchronized
    I get( final long index ) {
        int i = (int) ( index - currentLowerIndex );
        if ( i < 0 || i >= size ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            return array[i];
        }
    }

    public
    long getRealSize() {
        return realSize;
    }

    public
    int getSize() {
        return size;
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
    long lowerIndex() {
        return currentLowerIndex;
    }

    @Override
    public synchronized
    void set(
            final long index,
            final I value
    ) {
        int i = (int) ( index - currentLowerIndex );
        if ( i < 0 || i >= size ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            array[i] = value;
        }
    }

    @Override
    public synchronized
    void swap(
            long index1,
            long index2
    ) {
        I temp = get(index1);
        set(index1, get(index2));
        set(index2, temp);
    }

    @Override
    public
    long upperIndex( final int pid ) {
        return indexes.get(pid - 1).getUpperIndex();
    }

    @Override
    public
    long upperIndex() {
        return currentUpperIndex;
    }

    private
    class Indexes {
        private final long loweIndex;
        private final int  size;
        private final long upperIndex;

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
