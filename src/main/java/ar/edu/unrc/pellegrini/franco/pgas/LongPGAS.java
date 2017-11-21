package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ar.edu.unrc.pellegrini.franco.pgas.net.Message.*;
import static java.util.logging.Logger.getLogger;

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
    private final long               pgasSize;
    private final int                pid;
    private final int                processQuantity;
    private Long[] memory = null;


    public
    LongPGAS(
            final int pid,
            final String configsFilePath
    ) {
        this(pid, new File(configsFilePath), true);
    }

    public
    LongPGAS(
            final int pid,
            final String configsFilePath,
            final boolean startServer
    ) {
        this(pid, new File(configsFilePath), startServer);
    }

    public
    LongPGAS(
            final int pid,
            final File configsFile
    ) {
        this(pid, configsFile, true);
    }

    public
    LongPGAS(
            final int pid,
            final File configsFile,
            final boolean startServer
    ) {
        final Configs< Long > configFile = new Configs<>(configsFile);
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
            final HostConfig< Long > integerHostConfig = configFile.getHostsConfig(currentPid);
            final List< Long >       toSort            = integerHostConfig.getToSort();
            if ( pid == currentPid ) {
                memory = toSort.toArray(new Long[toSort.size()]);
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
        middleware = new LongMiddleware(this, configFile, startServer);
    }

    @Override
    public
    boolean andReduce( final boolean value )
            throws IOException, InterruptedException {
        boolean andReduce = value;
        if ( imCoordinator ) {
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                final Message msg = middleware.waitFor(pid, AND_REDUCE_MSG);
                andReduce = andReduce && ( msg.getResponse() != 0 ); //true!=0, false==0
            }
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                middleware.sendTo(pid, CONTINUE_MSG, ( andReduce ) ? 1L : 0L);
            }
        } else {
            middleware.sendTo(COORDINATOR_PID, AND_REDUCE_MSG, ( value ) ? 1L : 0L);
            final Message msg = middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
            andReduce = msg.getResponse() != 0;
        }
        return andReduce;
    }

    @Override
    public
    String asString() {
        return LongStream.range(0L, pgasSize).mapToObj(index -> {
            try {
                return Long.toString(read(index));
            } catch ( Exception e ) {
                getLogger(LongPGAS.class.getName()).log(Level.SEVERE, null, e);
                return "ERROR";
            }
        }).collect(Collectors.joining(","));
    }

    @Override
    public
    void barrier()
            throws IOException, InterruptedException {
        if ( imCoordinator ) {
            assert pid == 1;
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                //                System.out.println("Flag wait Barrier + pid=" + pid + " " + Thread.currentThread().getName());
                middleware.waitFor(pid, BARRIER_MSG);
            }
            for ( int pid = 2; pid <= processQuantity; pid++ ) {
                //                System.out.println("Flag send continue + pid=" + pid + " " + Thread.currentThread().getName());
                middleware.sendTo(pid, CONTINUE_MSG);
            }
        } else {
            assert pid >= 1;
            //            System.out.println("Flag send barrier + pid=" + pid + " " + Thread.currentThread().getName());
            middleware.sendTo(COORDINATOR_PID, BARRIER_MSG);
            //            System.out.println("Flag wait continue + pid=" + pid + " " + Thread.currentThread().getName());
            middleware.waitFor(COORDINATOR_PID, CONTINUE_MSG);
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
        return memory.length;
    }

    @Override
    public
    boolean imLast() {
        return pid == processQuantity;
    }

    public
    boolean isCoordinator() {
        return imCoordinator;
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
            throws IOException, InterruptedException {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.length ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(targetPid, READ_MSG, index);
            final Message response = middleware.waitFor(targetPid, READ_RESPONSE_MSG);
            return response.getResponse();
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
            throws IOException, InterruptedException {
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
