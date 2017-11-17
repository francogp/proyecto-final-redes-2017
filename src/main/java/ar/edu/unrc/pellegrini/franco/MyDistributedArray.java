package ar.edu.unrc.pellegrini.franco;

public
class MyDistributedArray< I >
        implements DistributedArray< I > {

    private final Middleware< I > middleware;
    private final int             pid;

    public
    MyDistributedArray( final Middleware< I > middleware ) {
        this.middleware = middleware;
        this.pid = middleware.getPid();
    }

    @Override
    public
    I get( final long index ) {
        return null;
    }

    @Override
    public
    boolean imLast() {
        return false;
    }

    @Override
    public
    long lowerIndex( final int pid ) {
        return 0;
    }

    @Override
    public
    void set(
            final long index,
            final I value
    ) {

    }

    @Override
    public
    void swap(
            long index1,
            long index2
    ) {

    }

    @Override
    public
    long upperIndex( final int pid ) {
        return 0;
    }
}
