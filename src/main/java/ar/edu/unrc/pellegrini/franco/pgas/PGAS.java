package ar.edu.unrc.pellegrini.franco.pgas;

import java.io.IOException;

public
interface PGAS< I > {

    boolean andReduce( final boolean value )
            throws IOException, InterruptedException;

    void barrier()
            throws IOException, InterruptedException;

    int getPid();

    int getSize();

    boolean imLast();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final Long index )
            throws IOException, InterruptedException;

    void swap(
            final long index1,
            final long index2
    )
            throws IOException, InterruptedException;

    long upperIndex();

    long upperIndex( final int pid );

    void write(
            final Long index,
            final I value
    )
            throws IOException;
}
