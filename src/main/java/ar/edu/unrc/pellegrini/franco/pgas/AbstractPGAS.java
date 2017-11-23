package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Host;
import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractPGAS< I extends Comparable< I > >
        implements PGAS< I > {
    //FIXME darray? renombrar bien
    protected static boolean debugMode = false;
    protected final boolean         coordinator;
    protected final long            currentLowerIndex;
    protected final long            currentUpperIndex;
    protected final List< Index >   indexList;
    protected final Middleware< I > middleware;
    protected final long            pgasSize;
    protected final int             pid;
    protected final int             processQuantity;
    private List< I > memory = null;

    protected
    AbstractPGAS(
            final int pid,
            final String configsFilePath
    ) {
        this(pid, new File(configsFilePath));
    }

    protected
    AbstractPGAS(
            final int pid,
            final File configsFile
    ) {
        this(pid, new NetConfiguration<>(configsFile));
    }

    protected
    AbstractPGAS(
            final int pid,
            final NetConfiguration< I > configFile
    ) {
        processQuantity = configFile.getProcessQuantity();
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid " + pid + " must be >= 0."); }
        if ( pid > processQuantity ) { throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file."); }
        this.pid = pid;
        coordinator = pid == COORDINATOR_PID;
        // inicializamos los indices lowerIndex y upperIndex
        indexList = new ArrayList<>(processQuantity);
        long lowerIndex = 0L;
        long upperIndex = -1L;
        for ( int currentPid = 1; currentPid <= processQuantity; currentPid++ ) {
            final Host< I > host   = configFile.getHostsConfig(currentPid);
            final List< I > toSort = host.getToSort();
            if ( pid == currentPid ) {
                memory = new ArrayList<>(toSort);
            }
            upperIndex = ( lowerIndex + ( toSort.size() ) ) - 1L;
            indexList.add(new Index(lowerIndex, upperIndex, toSort.size()));
            lowerIndex = upperIndex + 1L;
        }
        // inicializamos lowerIndex y upperIndex del proceso actual (a modo de cache)
        currentLowerIndex = lowerIndex(pid);
        currentUpperIndex = upperIndex(pid);
        pgasSize = upperIndex + 1L;
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = newMiddleware(configFile);
    }

    @Override
    public final
    boolean andReduce( final boolean value )
            throws IOException, InterruptedException {
        boolean andReduce = value;
        //FIXME pasar almiddleware
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Message< I > msg = middleware.waitFor(targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && parseResponseAsBoolean(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, CONTINUE_AND_REDUCE_MSG, 0L, booleanAsMessageParameter(andReduce));
            }
        } else {
            middleware.sendTo(COORDINATOR_PID, AND_REDUCE_MSG, 0L, booleanAsMessageParameter(value));
            final Message< I > msg = middleware.waitFor(COORDINATOR_PID, CONTINUE_AND_REDUCE_MSG);
            andReduce = parseResponseAsBoolean(msg);
        }
        return andReduce;
    }

    @Override
    public final
    void barrier()
            throws IOException, InterruptedException {
        //FIXME pasar almiddleware
        if ( coordinator ) {
            assert pid == 1;
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.waitFor(targetPid, BARRIER_MSG);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, CONTINUE_BARRIER_MSG, 0L, null);
            }
        } else {
            assert pid >= 1;
            middleware.sendTo(COORDINATOR_PID, BARRIER_MSG, 0L, null);
            middleware.waitFor(COORDINATOR_PID, CONTINUE_BARRIER_MSG);
        }
    }

    protected abstract
    I booleanAsMessageParameter( final boolean value );

    @Override
    public final
    void endService()
            throws IOException {
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, END_MSG, 0L, null);
            }
            middleware.sendTo(1, END_MSG, 0L, null);
        }
    }

    private
    int findPidForIndex( final long index ) {
        //TODO optimizar
        for ( int targetPid = 0; targetPid < processQuantity; targetPid++ ) {
            final Index indexItem = indexList.get(targetPid);
            if ( ( indexItem.loweIndex <= index ) && ( indexItem.upperIndex >= index ) ) {
                return targetPid + 1;
            }
        }
        return -1;
    }

    @Override
    public final
    long getPgasSize() {
        return pgasSize;
    }

    @Override
    public final
    int getPid() {
        return pid;
    }

    @Override
    public final
    int getSize() {
        return memory.size();
    }

    @Override
    public final
    boolean imLast() {
        return pid == processQuantity;
    }

    @Override
    public final
    boolean isCoordinator() {
        return coordinator;
    }

    @Override
    public final
    long lowerIndex( final int pid ) {
        return indexList.get(pid - 1).getLoweIndex();
    }

    @Override
    public final
    long lowerIndex() {
        return currentLowerIndex;
    }

    protected abstract
    Middleware< I > newMiddleware(
            final NetConfiguration< I > configFile
    );

    protected abstract
    boolean parseResponseAsBoolean( final Message< I > message );

    @Override
    public final
    I read( final Long index )
            throws IOException, InterruptedException {
        //FIXME synchronized?
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, READ_MSG, index, null);
            final Message< I > response = middleware.waitFor(targetPid, READ_RESPONSE_MSG);
            return response.getValueParameter();
        } else {
            synchronized ( memory ) {
                return memory.get(i);
            }
        }
    }

    @Override
    public final
    void setDebugMode( final boolean mode ) {
        debugMode = mode;
        middleware.setDebugMode(mode);
    }

    @Override
    public final
    void startServer() {
        middleware.startServer();
    }

    @Override
    public final
    void swap(
            final long index1,
            final long index2
    )
            throws IOException, InterruptedException {
        //FIXME synchronized?
        final I temp = read(index1);
        write(index1, read(index2));
        write(index2, temp);
    }

    @Override
    public final
    long upperIndex( final int pid ) {
        return indexList.get(pid - 1).getUpperIndex();
    }

    @Override
    public final
    long upperIndex() {
        return currentUpperIndex;
    }

    @Override
    public synchronized final
    void write(
            final Long index,
            final I value
    )
            throws IOException {
        //FIXME synchronized?
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, WRITE_MSG, index, value);
        } else {
            synchronized ( memory ) {
                memory.set(i, value);
            }
        }
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static final
    class Index {
        final long loweIndex;
        final int  size;
        final long upperIndex;

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

        @Override
        public
        String toString() {
            return "Index{" + "loweIndex=" + loweIndex + ", size=" + size + ", upperIndex=" + upperIndex + '}';
        }
    }
}
