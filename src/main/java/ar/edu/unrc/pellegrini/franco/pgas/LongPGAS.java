package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.pgas.net.Message.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongPGAS
        implements PGAS< Long > {

    public static final int COORDINATOR_PID = 1;
    private final long               currentLowerIndex;
    private final long               currentUpperIndex;
    private final boolean            imCoordinator;
    private final List< Index >      indexList;
    private final Middleware< Long > middleware;
    private final int                pid;
    private final int                processQuantity;
    private Long[] memory = null;

    public
    LongPGAS(
            final int pid,
            final Configs< Long > configs
    ) {
        this(pid, configs, true);
    }

    public
    LongPGAS(
            final int pid,
            final Configs< Long > configs,
            final boolean startServer
    ) {
        processQuantity = configs.getProcessQuantity();
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid " + pid + " must be >= 0."); }
        if ( pid > processQuantity ) { throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file."); }
        this.pid = pid;
        imCoordinator = pid == COORDINATOR_PID;
        // inicializamos los indices lowerIndex y upperIndex
        indexList = new ArrayList<>(processQuantity);
        long lowerIndex = 0L;
        for ( int currentPid = 1; currentPid <= processQuantity; currentPid++ ) {
            final HostConfig< Long > integerHostConfig = configs.getHostsConfig(currentPid);
            final List< Long >       toSort            = integerHostConfig.getToSort();
            if ( pid == currentPid ) {
                memory = toSort.toArray(new Long[toSort.size()]);
            }
            final long upperIndex = ( lowerIndex + ( toSort.size() ) ) - 1L;
            indexList.add(new Index(lowerIndex, upperIndex, toSort.size()));
            lowerIndex = upperIndex + 1L;
        }
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = new LongMiddleware(this, configs, startServer);
    }

    @Override
    public
    boolean andReduce( final boolean value ) {
        return false;
    }

    @Override
    public
    void barrier()
            throws IOException {
        if ( imCoordinator ) {
            for ( int pid = 1; pid <= processQuantity; pid++ ) {
                middleware.waitFor(pid, BARRIER_MSG);
            }
            for ( int pid = 1; pid <= processQuantity; pid++ ) {
                middleware.sendTo(pid, CONTINUE_MSG);
            }
        } else {
            middleware.sendTo(COORDINATOR_PID, BARRIER_MSG);
            middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
        }
    }

    private
    int findPidForIndex( final long index ) {
        //TODO optimizar
        for ( int pid = 1; pid <= processQuantity; pid++ ) {
            Index indexItem = indexList.get(pid);
            if ( indexItem.loweIndex <= index && indexItem.upperIndex >= index ) {
                return pid;
            }
        }
        return -1;
    }

    public
    int getPid() {
        return pid;
    }

    public synchronized
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
        return indexList.get(pid - 1).getLoweIndex();
    }

    @Override
    public
    long lowerIndex() {
        return currentLowerIndex;
    }

    @Override
    public synchronized
    Long read( final long index )
            throws IOException {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.length ) ) {
            final int targetPid = findPidForIndex(index);
            Message   response  = middleware.waitFor(targetPid, READ_MSG, index);
            return response.getParameter2(); //TODO verificar diseño
        } else {
            return memory[i];
        }
    }

    @Override
    public synchronized
    void swap(
            final long index1,
            final long index2
    )
            throws IOException {
        //TODO REVISAR LOS SYNCHRONIZED!
        final Long temp = read(index1);
        write(index1, read(index2));
        write(index2, temp);
    }

    @Override
    public
    long upperIndex( final int pid ) {
        return indexList.get(pid - 1).getUpperIndex();
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
    )
            throws IOException {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.length ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, WRITE_MSG, index, value);
        } else {
            memory[i] = value;
        }
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static
    class Index {
        private final long loweIndex;
        private final int  size;
        private final long upperIndex;

        public
        Index(
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
