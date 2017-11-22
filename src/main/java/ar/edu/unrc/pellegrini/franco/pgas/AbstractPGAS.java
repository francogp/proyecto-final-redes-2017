package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.Host;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractPGAS< I extends Comparable< I > >
        implements PGAS< I > {

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
        this(pid, new File(configsFilePath), true);
    }

    protected
    AbstractPGAS(
            final int pid,
            final String configsFilePath,
            final boolean startServer
    ) {
        this(pid, new File(configsFilePath), startServer);
    }

    protected
    AbstractPGAS(
            final int pid,
            final File configsFile
    ) {
        this(pid, configsFile, true);
    }

    protected
    AbstractPGAS(
            final int pid,
            final File configsFile,
            final boolean startServer
    ) {
        final NetConfiguration< I > configFile = new NetConfiguration<>(configsFile);
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
        middleware = newMiddleware(startServer, configFile);
    }

    public final
    boolean andReduce( final boolean value )
            throws IOException, InterruptedException {
        boolean andReduce = value;
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Message< I > msg = middleware.waitFor(targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && parseResponseAsBoolean(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, CONTINUE_MSG, null, booleanAsMessageParameter(andReduce));
            }
        } else {
            middleware.sendTo(COORDINATOR_PID, AND_REDUCE_MSG, null, booleanAsMessageParameter(value));
            final Message< I > msg = middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
            andReduce = parseResponseAsBoolean(msg);
        }
        return andReduce;
    }

    public final
    void barrier()
            throws IOException, InterruptedException {
        if ( coordinator ) {
            assert pid == 1;
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.waitFor(targetPid, BARRIER_MSG);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, CONTINUE_MSG, null, null);
            }
        } else {
            assert pid >= 1;
            middleware.sendTo(COORDINATOR_PID, BARRIER_MSG, null, null);
            middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
        }
    }

    protected abstract
    I booleanAsMessageParameter( final boolean value );

    public final
    void endService()
            throws IOException {
        if ( coordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, END_MSG, null, null);
            }
            middleware.sendTo(1, END_MSG, null, null);
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

    public final
    long getPgasSize() {
        return pgasSize;
    }

    public final
    int getPid() {
        return pid;
    }

    public final
    int getSize() {
        return memory.size();
    }

    public final
    boolean imLast() {
        return pid == processQuantity;
    }

    @Override
    public final
    boolean isCoordinator() {
        return coordinator;
    }

    public final
    long lowerIndex( final int pid ) {
        return indexList.get(pid - 1).getLoweIndex();
    }

    public final
    long lowerIndex() {
        return currentLowerIndex;
    }

    protected abstract
    Middleware< I > newMiddleware(
            final boolean startServer,
            final NetConfiguration< I > configFile
    );

    protected abstract
    boolean parseResponseAsBoolean( final Message< I > message );

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
            return memory.get(i);
        }
    }

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

    public final
    long upperIndex( final int pid ) {
        return indexList.get(pid - 1).getUpperIndex();
    }

    public final
    long upperIndex() {
        return currentUpperIndex;
    }

    public final
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
            memory.set(i, value);
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
