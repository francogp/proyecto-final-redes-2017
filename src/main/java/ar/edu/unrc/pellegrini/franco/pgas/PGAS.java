package ar.edu.unrc.pellegrini.franco.pgas;

import java.io.IOException;

public
interface PGAS< I > {

    String asString();

    int getName();

    long getPgasSize();

    int getSize();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final Long index )
            throws IOException, InterruptedException;

    void setDebugMode( boolean mode );

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
