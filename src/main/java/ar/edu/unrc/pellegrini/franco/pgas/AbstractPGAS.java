package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
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
        pgasSize = upperIndex + 1L;
        // indicamos al middleware quien es el arreglo distribuido a utilizar
        middleware = newMiddleware(startServer, configFile);
    }

    public
    boolean andReduce( final boolean value )
            throws IOException, InterruptedException {
        boolean andReduce = value;
        if ( imCoordinator ) {
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                final Message< I > msg = middleware.waitFor(targetPid, AND_REDUCE_MSG);
                andReduce = andReduce && responseToBooleanRepresentation(msg);
            }
            for ( int targetPid = 2; targetPid <= processQuantity; targetPid++ ) {
                middleware.sendTo(targetPid, CONTINUE_MSG, booleanAsMessageParameter(andReduce), null);
            }
        } else {
            middleware.sendTo(COORDINATOR_PID, AND_REDUCE_MSG, booleanAsMessageParameter(value), null);
            final Message< I > msg = middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
            andReduce = responseToBooleanRepresentation(msg);
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

    protected abstract
    I booleanAsMessageParameter( final boolean value );

    public
    void endService()
            throws IOException {
        if ( imCoordinator ) {
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                middleware.sendTo(pid, END_MSG, null, null);
            }
            middleware.sendTo(1, END_MSG, null, null);
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

    public
    int getSize() {
        return memory.size();
    }

    public
    boolean imLast() {
        return pid == processQuantity;
    }

    protected abstract
    I indexAsMessageParameter( final Long index );

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
            final boolean startServer,
            final Configs< I > configFile
    );

    public
    I read( final Long index )
            throws IOException, InterruptedException {
        //FIXME synchonized?
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, READ_MSG, indexAsMessageParameter(index), null);
            final Message< I > response = middleware.waitFor(targetPid, READ_RESPONSE_MSG);
            return response.getResponse();
        } else {
            return memory.get(i);
        }
    }

    protected abstract
    boolean responseToBooleanRepresentation( final Message< I > bool );

    public
    void swap(
            final long index1,
            final long index2
    )
            throws IOException, InterruptedException {
        //FIXME synchonized?
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

    public
    void write(
            final Long index,
            final I value
    )
            throws IOException {
        //FIXME synchonized?
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, WRITE_MSG, indexAsMessageParameter(index), value);
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
