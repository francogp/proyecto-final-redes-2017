package ar.edu.unrc.pellegrini.franco;

public
class MyMiddleware< I >
        implements Middleware< I > {
    private final int pid;

    public
    MyMiddleware( final int pid ) {
        this.pid = pid;
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
