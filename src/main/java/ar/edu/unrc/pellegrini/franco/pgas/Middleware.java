package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {

    //    void receiveFrom( final int pid );
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType
    )
            throws IOException;

    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType,
            final long parameter1
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            char msgType,
            final long parameter1
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            char msgType
    )
            throws IOException;

    Message waitFor(
            final int senderPid,
            char msgType
    )
            throws IOException, InterruptedException;

}

