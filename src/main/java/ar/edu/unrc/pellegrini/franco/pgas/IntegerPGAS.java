package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.util.ArrayList;
import java.util.List;

public
class IntegerPGAS
        implements PGAS< Integer > {

    private final long                  currentLowerIndex;
    private final long                  currentUpperIndex;
    private final List< Indexes >       indexes;
    private final Integer[]             memory;
    private final Middleware< Integer > middleware;
    private final long                  pgasSize;
    private final int                   pid;
    private final int                   processQuantity;
    private final int                   size;

    public
    IntegerPGAS(
            final int pid,
            final Configs configs
    ) {
        this.pid = pid;
        pgasSize = configs.getPgasSize();
        processQuantity = configs.getProcessQuantity();
        //iniciamos el tamaño que debería tener la porción del arreglo correspondiente al proceso distribuido actual
        long actualSize = pgasSize / processQuantity;
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
        actualSize = pgasSize - ( actualSize * ( processQuantity - 1 ) );
        upperIndex = ( lowerIndex + actualSize ) - 1;
        indexes.add(new Indexes(lowerIndex, upperIndex, (int) actualSize));
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        // inicializamos el arreglo del proceso actual
        size = indexes.get(pid - 1).getSize();
        memory = new Integer[size];
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        this.middleware = new IntegerMiddleware(this, configs);
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

    public
    long getPgasSize() {
        return pgasSize;
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
    Integer read( final long index ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= size ) ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            return memory[i];
        }
    }

    @Override
    public synchronized
    void swap(
            final long index1,
            final long index2
    ) {
        //TODO REVISAR LOS SYNCHRONIZED!
        final Integer temp = read(index1);
        write(index1, read(index2));
        write(index2, temp);
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

    @Override
    public synchronized
    void write(
            final long index,
            final Integer value
    ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= size ) ) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            memory[i] = value;
        }
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
