package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;
import ar.edu.unrc.pellegrini.franco.net.Process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class DistributedArray< I extends Comparable< I > >
        implements PGAS< I > {
    protected static boolean debugMode = false;
    protected final long            currentLowerIndex;
    protected final long            currentUpperIndex;
    protected final List< Index >   indexList;
    protected final Middleware< I > middleware;
    protected final long            pgasSize;
    private List< I > memory = null;
    private int name;

    protected
    DistributedArray(
            final int name,
            final String configsFilePath,
            final Middleware< I > middleware
    ) {
        this(name, new File(configsFilePath), middleware);
    }

    protected
    DistributedArray(
            final int name,
            final File configsFile,
            final Middleware< I > middleware
    ) {
        this(name, new NetConfiguration<>(configsFile), middleware);
    }

    public
    DistributedArray(
            final int name,
            final NetConfiguration< I > configFile,
            final Middleware< I > middleware
    ) {
        this.middleware = middleware;
        this.name = name;
        int pid = middleware.getPid();

        // inicializamos los indices lowerIndex y upperIndex
        indexList = new ArrayList<>(middleware.getProcessQuantity());
        long lowerIndex = 0L;
        long upperIndex = -1L;
        for ( int currentPid = 1; currentPid <= middleware.getProcessQuantity(); currentPid++ ) {
            final Process< I > process = configFile.getProcessConfig(currentPid);
            final List< I >    toSort  = process.getToSort();
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

        middleware.registerPGAS(this);
    }

    @Override
    public
    String asString() {
        return LongStream.range(0L, pgasSize).mapToObj(index -> {
            try {
                return read(index).toString();
            } catch ( Exception e ) {
                getLogger(DistributedArray.class.getName()).log(Level.SEVERE, null, e);
                return "ERROR";
            }
        }).collect(Collectors.joining(", "));
    }

    private
    int findPidForIndex( final long index ) {
        for ( int targetPid = 0; targetPid < middleware.getProcessQuantity(); targetPid++ ) {
            final Index indexItem = indexList.get(targetPid);
            if ( ( indexItem.loweIndex <= index ) && ( indexItem.upperIndex >= index ) ) {
                return targetPid + 1;
            }
        }
        return -1;
    }

    @Override
    public
    int getName() {
        return name;
    }

    @Override
    public final
    long getPgasSize() {
        return pgasSize;
    }

    @Override
    public final
    int getSize() {
        return memory.size();
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

    @Override
    public final
    I read( final long index )
            throws Exception {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(name, targetPid, READ_MSG, index, null);
            final Message< I > response = middleware.waitFor(name, targetPid, READ_RESPONSE_MSG);
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
    void swap(
            final long index1,
            final long index2
    )
            throws Exception {
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
            final long index,
            final I value
    )
            throws Exception {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= memory.size() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(name, targetPid, WRITE_MSG, index, value);
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
