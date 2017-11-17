package ar.edu.unrc.pellegrini.franco;

public
interface Middleware< I > {
    boolean andReduce( final boolean value );

    void barrier();

    void get( final long index );

    int getPid();

    void receiveFrom( final long pid );

    void sendTo(
            final long pid,
            final I value
    );

    void set(
            final long index,
            final I value
    );
}

