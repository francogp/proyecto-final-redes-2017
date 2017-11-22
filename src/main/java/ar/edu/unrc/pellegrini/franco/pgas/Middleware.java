package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.utils.Host;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {
    public static final boolean DEBUG_MODE = true;

    void sendTo(
            final Host< I > targetHost,
            final MessageType msgType,
            final I parameter1,
            final I parameter2
    )
            throws IOException;

    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final I parameter1,
            final I parameter2
    )
            throws IOException;

    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException;
}
