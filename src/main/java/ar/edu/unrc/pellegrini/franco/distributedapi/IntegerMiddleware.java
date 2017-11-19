package ar.edu.unrc.pellegrini.franco.distributedapi;

public
class IntegerMiddleware
        implements Middleware< Integer > {
    private final DistributedArray< Integer > distArray;

    public
    IntegerMiddleware(
            final DistributedArray< Integer > distArray
    ) {
        this.distArray = distArray;
    }

    @Override
    public
    void get( final long index ) {

    }

    @Override
    public
    void receiveFrom( final long pid ) {

    }

    @Override
    public
    void sendTo(
            final long pid,
            final Integer value
    ) {

    }

    @Override
    public
    void set(
            final long index,
            final Integer value
    ) {

    }


}
