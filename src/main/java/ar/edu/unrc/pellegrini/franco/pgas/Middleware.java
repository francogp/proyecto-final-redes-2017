package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {

    void receiveFrom( final int pid );

    void sendTo(
            final int pid,
            char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    void sendTo(
            final int pid,
            char msgType,
            final long parameter1
    )
            throws IOException;

    void sendTo(
            final int pid,
            char msgType
    )
            throws IOException;

    Message waitFor(
            final int pid,
            char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    Message waitFor(
            final int pid,
            char msgType,
            final long parameter1
    )
            throws IOException;

    Message waitFor(
            final int pid,
            char msgType
    )
            throws IOException;

}

