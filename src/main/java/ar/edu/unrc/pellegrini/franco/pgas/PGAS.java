package ar.edu.unrc.pellegrini.franco.pgas;

public
interface PGAS< I > {

    boolean andReduce( final boolean value );

    void barrier();

    int getSize();

    boolean imLast();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final long index );

    void swap(
            final long index1,
            final long index2
    );

    long upperIndex();

    long upperIndex( final int pid );

    void write(
            final long index,
            final I value
    );
}
