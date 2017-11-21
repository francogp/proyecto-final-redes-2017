package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {

    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException;

    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType
    )
            throws IOException;

    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType,
            final long parameter1
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long parameter1
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            final MessageType msgType
    )
            throws IOException;

    Message waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws IOException, InterruptedException;

}

