package ar.edu.unrc.pellegrini.franco.distributedapi;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.util.ArrayList;
import java.util.List;

public
class IntegerDistributedArray
        implements DistributedArray< Integer > {

    private final Integer[]             array;
    private final long                  currentLowerIndex;
    private final long                  currentUpperIndex;
    private final long                  distributedSize;
    private final List< Indexes >       indexes;
    private final Middleware< Integer > middleware;
    private final int                   pid;
    private final int                   processQuantity;
    private final int                   size;

    public
    IntegerDistributedArray(
            final int pid,
            final Configs configs
    ) {
        this.pid = pid;
        distributedSize = configs.getDistributedArraySize();
        processQuantity = configs.getProcessQuantity();
        //iniciamos el tamaño que debería tener la porción del arreglo correspondiente al proceso distribuido actual
        long actualSize = distributedSize / processQuantity;
        if ( actualSize > Integer.MAX_VALUE ) {
            throw new IllegalArgumentException("distributed process quantity is too small");
        }
        // inicializamos los indices lowerIndex y upperIndex
        indexes = new ArrayList<>(processQuantity);
        long lowerIndex = 0;
        long upperIndex;
        for ( int i = 0; i < ( processQuantity - 1 ); i++ ) {
            upperIndex = ( lowerIndex + actualSize ) - 1;
            indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
            lowerIndex = upperIndex + 1;
        }
        // inicializamos lowerIndex y upperIndex para el ultimo caso, donde colocamos el resto, en caso que la division al calcular actualSize no
        // sea equitativa entre todos los procesos distribuidos.
        actualSize = distributedSize - ( actualSize * ( processQuantity - 1 ) );
        upperIndex = ( lowerIndex + actualSize ) - 1;
        indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        // inicializamos el arreglo del proceso actual
        size = indexes.get(pid - 1).getSize();
        array = new Integer[size];//new Object[size];
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        this.middleware = new IntegerMiddleware(this);
    }

    @Override
    public
    boolean andReduce( boolean value ) {
        return false;
    }

    @Override
    public
    void barrier() {

    }

    @Override
    public synchronized
    Integer get( final long index ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= size ) ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            return array[i];
        }
    }

    public
    long getDistributedSize() {
        return distributedSize;
    }

    public
    int getPid() {
        return pid;
    }

    public
    int getProcessQuantity() {
        return processQuantity;
    }

    public
    int getSize() {
        return size;
    }

    @Override
    public
    boolean imLast() {
        return pid == processQuantity;
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
            final Integer value
    ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= size ) ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            array[i] = value;
        }
    }

    @Override
    public synchronized
    void swap(
            final long index1,
            final long index2
    ) {
        final Integer temp = get(index1);
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

    private static
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
