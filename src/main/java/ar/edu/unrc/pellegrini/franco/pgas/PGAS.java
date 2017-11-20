package ar.edu.unrc.pellegrini.franco.pgas;

import java.io.IOException;

public
interface PGAS< I > {

    boolean andReduce( final boolean value );

    void barrier()
            throws IOException;

    int getPid();

    int getSize();

    boolean imLast();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final long index )
            throws IOException;

    void swap(
            final long index1,
            final long index2
    )
            throws IOException;

    long upperIndex();

    long upperIndex( final int pid );

    void write(
            final long index,
            final I value
    )
            throws IOException;
}
