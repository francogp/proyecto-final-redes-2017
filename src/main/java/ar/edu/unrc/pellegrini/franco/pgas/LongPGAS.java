package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.util.ArrayList;
import java.util.List;

public
class LongPGAS
        implements PGAS< Long > {

    private final long               currentLowerIndex;
    private final long               currentUpperIndex;
    private final List< Indexes >    indexes;
    private final Middleware< Long > middleware;
    private final int                pid;
    private final int                processQuantity;
    private       Long[]             memory;
    public
    LongPGAS(
            final int pid,
            final Configs< Long > configs
    ) {
        processQuantity = configs.getProcessQuantity();
        if ( pid > processQuantity ) {
            throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file.");
        }
        this.pid = pid;
        // inicializamos los indices lowerIndex y upperIndex
        indexes = new ArrayList<>(processQuantity);
        long lowerIndex = 0;
        long upperIndex;
        for ( long currentPid = 1L; currentPid <= processQuantity; currentPid++ ) {
            Configs.HostConfig< Long > integerHostConfig = configs.getHostsConfig(currentPid);
            List< Long >               toSort            = integerHostConfig.getToSort();
            if ( pid == currentPid ) {
                memory = toSort.toArray(new Long[0]);
            }
            upperIndex = ( lowerIndex + ( toSort.size() ) ) - 1;
            indexes.add(new Indexes(lowerIndex, upperIndex, toSort.size()));
            lowerIndex = upperIndex + 1;
        }
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        this.middleware = new LongMiddleware(this, configs);
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
    int getPid() {
        return pid;
    }

    public
    int getSize() {
        return memory.length;
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
    Long read( final long index ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.length ) ) {
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
        final Long temp = read(index1);
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
            final Long value
    ) {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.length ) ) {
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
