package ar.edu.unrc.pellegrini.franco;

public
interface DistributedArray< I > {

    I get( final long index );

    boolean imLast();

    long lowerIndex( final int pid );

    void set(
            final long index,
            final I value
    );

    void swap(
            final long index1,
            final long index2
    );

    long upperIndex( final int pid );
}
