package ar.edu.unrc.pellegrini.franco.distributedapi;

public
interface Middleware< I > {

    void get( final long index );

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

