package ar.edu.unrc.pellegrini.franco.pgas;

public
interface PGAS< I > {

    String asString();

    int getName();

    long getPgasSize();

    int getSize();

    long lowerIndex();

    long lowerIndex( final int pid );

    I read( final long index )
            throws Exception;

    void setDebugMode( boolean mode );

    void swap(
            final long index1,
            final long index2
    )
            throws Exception;

    long upperIndex();

    long upperIndex( final int pid );

    void write(
            final long index,
            final I value
    )
            throws Exception;
}
