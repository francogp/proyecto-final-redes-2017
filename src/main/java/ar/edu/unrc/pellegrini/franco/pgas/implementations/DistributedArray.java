package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static java.util.logging.Logger.getLogger;

/**
 * A PGAS implementation using a Distributed Array logic.
 *
 * @param <I> value type carried by the Message.
 */
@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedArray< I >
        implements PGAS< I > {
    private final long currentLowerIndex;
    private final long currentUpperIndex;
    private final List< I > distArray = new ArrayList<>();
    private final List< Index >   indexList;
    private final Middleware< I > middleware;
    private final int             name;
    private final long            pgasSize;

    /**
     * @param name       PGAS unique name.
     * @param middleware to register this PGAS.
     */
    public
    DistributedArray(
            final int name,
            final Middleware< I > middleware
    ) {
        this.middleware = middleware;
        this.name = name;
        final int pid             = middleware.getWhoAmI();
        final int processQuantity = middleware.getProcessQuantity();

        // inicializamos los indices lowerIndex y upperIndex
        indexList = new ArrayList<>(processQuantity);
        long lowerIndex = 0L;
        long upperIndex = -1L;
        for ( int currentPid = 1; currentPid <= processQuantity; currentPid++ ) {
            final List< I > processValues = middleware.getProcessConfiguration(currentPid).getValues(name);
            if ( pid == currentPid ) {
                distArray.addAll(processValues);
            }
            upperIndex = ( lowerIndex + ( processValues.size() ) ) - 1L;
            indexList.add(new Index(lowerIndex, upperIndex, processValues.size()));
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
        if ( middleware.isCoordinator() ) {
            return LongStream.range(0L, pgasSize).mapToObj(index -> {
                try {
                    return read(index).toString();
                } catch ( Exception e ) {
                    getLogger(DistributedArray.class.getName()).log(Level.SEVERE, null, e);
                    return "ERROR";
                }
            }).collect(Collectors.joining(", "));
        } else {
            return null;
        }
    }

    private
    int findPidForIndex( final long index ) {
        final int processQuantity = middleware.getProcessQuantity();
        for ( int targetPid = 0; targetPid < processQuantity; targetPid++ ) {
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
    int getSize() {
        synchronized ( distArray ) {
            return distArray.size();
        }
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
        if ( ( i < 0 ) || ( i >= getSize() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(name, targetPid, READ_MSG, index, null);
            final Message< I > response = middleware.receiveFrom(name, targetPid, READ_RESPONSE_MSG);
            return response.getValue();
        } else {
            synchronized ( distArray ) {
                return distArray.get(i);
            }
        }
    }

    @Override
    public final
    void setDebugMode( final boolean enable ) {
        middleware.setDebugMode(enable);
    }

    @Override
    public final
    void swap(
            final long index1,
            final long index2
    )
            throws Exception {
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
    public final synchronized
    void write(
            final long index,
            final I value
    )
            throws Exception {
        final int i = (int) ( index - currentLowerIndex );
        if ( ( i < 0 ) || ( i >= getSize() ) ) {
            final int targetPid = findPidForIndex(index);
            middleware.sendTo(name, targetPid, WRITE_MSG, index, value);
        } else {
            synchronized ( distArray ) {
                distArray.set(i, value);
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
