package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.*;

public abstract
class AbstractPGAS< I extends Comparable< I > >
        implements PGAS< I > {

    public static final int COORDINATOR_PID = 1;
    protected final long            currentLowerIndex;
    protected final long            currentUpperIndex;
    protected final boolean         imCoordinator;
    protected final List< Index >   indexList;
    protected final Middleware< I > middleware;
    protected final long            pgasSize;
    protected final int             pid;
    protected final int             processQuantity;
    protected List< I > memory = null;


    public
    AbstractPGAS(
            final int pid,
            final String configsFilePath
    ) {
        this(pid, new File(configsFilePath), true);
    }

    public
    AbstractPGAS(
            final int pid,
            final String configsFilePath,
            final boolean startServer
    ) {
        this(pid, new File(configsFilePath), startServer);
    }

    public
    AbstractPGAS(
            final int pid,
            final File configsFile
    ) {
        this(pid, configsFile, true);
    }

    public
    AbstractPGAS(
            final int pid,
            final File configsFile,
            final boolean startServer
    ) {
        final Configs< I > configFile = new Configs<>(configsFile);
        processQuantity = configFile.getProcessQuantity();
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid " + pid + " must be >= 0."); }
        if ( pid > processQuantity ) { throw new IllegalArgumentException("pid " + pid + " is greater than defined in config file."); }
        this.pid = pid;
        imCoordinator = pid == COORDINATOR_PID;
        // inicializamos los indices lowerIndex y upperIndex
        indexList = new ArrayList<>(processQuantity);
        long lowerIndex = 0L;
        long upperIndex = -1L;
        for ( int currentPid = 1; currentPid <= processQuantity; currentPid++ ) {
            final HostConfig< I > integerHostConfig = configFile.getHostsConfig(currentPid);
            final List< I >       toSort            = integerHostConfig.getToSort();
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
        pgasSize = upperIndex + 1;
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = newMiddleware(startServer, configFile);
    }

    public
    boolean andReduce( final boolean value )
            throws IOException, InterruptedException {
        boolean andReduce = value;
        assert COORDINATOR_PID == 1;
        if ( imCoordinator ) {
            assert pid == 1;
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Message< I > msg = middleware.waitFor(targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && responseAsBooleanRepresentation(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Long reduceAsLong = ( andReduce ) ? 1L : 0L;
                middleware.sendTo(targetPid, CONTINUE_MSG, (I) reduceAsLong, null);
            }
        } else {
            assert pid != 1;
            final Long valueAsLong = ( value ) ? 1L : 0L;
            middleware.sendTo(COORDINATOR_PID, AND_REDUCE_MSG, (I) valueAsLong, null);
            final Message< I > msg = middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
            andReduce = responseAsBooleanRepresentation(msg);
        }
        return andReduce;
    }

    public abstract
    String asString();

    public
    void barrier()
            throws IOException, InterruptedException {
        if ( imCoordinator ) {
            assert pid == 1;
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                middleware.waitFor(pid, BARRIER_MSG);
            }
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                middleware.sendTo(pid, CONTINUE_MSG, null, null);
            }
        } else {
            assert pid >= 1;
            middleware.sendTo(COORDINATOR_PID, BARRIER_MSG, null, null);
            middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
        }
    }

    public
    void endService()
            throws IOException {
        for ( int pid = 1; pid <= processQuantity; pid++ ) {
            middleware.sendTo(pid, END_MSG, null, null);
        }
    }

    private
    int findPidForIndex( final long index ) {
        //TODO optimizar
        for ( int pid = 0; pid < processQuantity; pid++ ) {
            final Index indexItem = indexList.get(pid);
            if ( ( indexItem.loweIndex <= index ) && ( indexItem.upperIndex >= index ) ) {
                return pid + 1;
            }
        }
        return -1;
    }

    public
    long getPgasSize() {
        return pgasSize;
    }

    public
    int getPid() {
        return pid;
    }

    public synchronized
    int getSize() {
        return memory.size();
    }

    public
    boolean imLast() {
        return pid == processQuantity;
    }

    public
    boolean isCoordinator() {
        return imCoordinator;
    }

    public
    long lowerIndex( final int pid ) {
        return indexList.get(pid - 1).getLoweIndex();
    }

    public
    long lowerIndex() {
        return currentLowerIndex;
    }

    protected abstract
    Middleware< I > newMiddleware(
            boolean startServer,
            Configs< I > configFile
    );

    public synchronized
    I read( final Long index )
            throws IOException, InterruptedException {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, READ_MSG, (I) index, null);
            final Message< I > response = middleware.waitFor(targetPid, READ_RESPONSE_MSG);
            return response.getResponse();
        } else {
            return memory.get(i);
        }
    }

    protected abstract
    boolean responseAsBooleanRepresentation( Message< I > bool );

    public synchronized
    void swap(
            final long index1,
            final long index2
    )
            throws IOException, InterruptedException {
        //TODO REVISAR LOS SYNCHRONIZED!
        final I temp = read(index1);
        write(index1, read(index2));
        write(index2, temp);
    }

    public
    long upperIndex( final int pid ) {
        return indexList.get(pid - 1).getUpperIndex();
    }

    public
    long upperIndex() {
        return currentUpperIndex;
    }

    public synchronized
    void write(
            final Long index,
            final I value
    )
            throws IOException {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, WRITE_MSG, (I) index, value);
        } else {
            memory.set(i, value);
        }
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static final
    class Index {
        protected final long loweIndex;
        protected final int  size;
        protected final long upperIndex;

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
