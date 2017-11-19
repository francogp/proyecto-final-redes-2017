package ar.edu.unrc.pellegrini.franco.pgas;

public
interface Middleware< I > {

    String getHostByPid( long pid );

    void receiveFrom( final long pid );

    void sendTo(
            final long pid,
            final I value
    );

}

