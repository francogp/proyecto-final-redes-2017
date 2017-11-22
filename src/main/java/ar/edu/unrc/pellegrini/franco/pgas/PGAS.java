package ar.edu.unrc.pellegrini.franco.pgas;

import java.io.IOException;

public
interface PGAS< I > {

    public static final int COORDINATOR_PID = 1;

    boolean andReduce( final boolean value )
            throws IOException, InterruptedException;

    String asString();

    void barrier()
            throws IOException, InterruptedException;

    void endService()
            throws IOException;

    long getPgasSize();

    int getPid();

    int getSize();

    boolean imLast();

    boolean isCoordinator();

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
