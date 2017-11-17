package ar.edu.unrc.pellegrini.franco;

public
class MyMiddleware< I >
        implements Middleware< I > {
    private final int pid;
    private final int processQuantity;

    public
    MyMiddleware(
            final int pid,
            final int processQuantity
    ) {
        if ( pid <= 0 ) { throw new IllegalArgumentException("pid must be > 0"); }
        this.pid = pid;
        this.processQuantity = processQuantity;
    }

    @Override
    public
    boolean andReduce( final boolean value ) {
        return false;
    }

    @Override
    public
    void barrier() {

    }

    @Override
    public
    void get( final long index ) {

    }

    @Override
    public
    int getPid() {
        return pid;
    }

    @Override
    public
    int getProcessQuantity() {
        return processQuantity;
    }

    @Override
    public
    void receiveFrom( final long pid ) {

    }

    @Override
    public
    void sendTo(
            final long pid,
            final I value
    ) {

    }

    @Override
    public
    void set(
            final long index,
            final I value
    ) {

    }


}
