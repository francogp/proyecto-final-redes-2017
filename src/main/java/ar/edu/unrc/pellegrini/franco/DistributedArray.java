package ar.edu.unrc.pellegrini.franco;

public
interface DistributedArray< I > {

    I get( final long index );

    long getRealSize();

    int getSize();

    boolean imLast();

    long lowerIndex();

    long lowerIndex( final int pid );

    void set(
            final long index,
            final I value
    );

    void swap(
            final long index1,
            final long index2
    );

    long upperIndex();

    long upperIndex( final int pid );
}
