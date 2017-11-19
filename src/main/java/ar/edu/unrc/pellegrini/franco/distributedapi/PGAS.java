package ar.edu.unrc.pellegrini.franco.distributedapi;

public
interface PGAS< I > {

    boolean andReduce( final boolean value );

    void barrier();

    long getPgasSize();

    int getSize();

    boolean imLast();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final long index );

    long upperIndex();

    long upperIndex( final int pid );

    void write(
            final long index,
            final I value
    );
}
