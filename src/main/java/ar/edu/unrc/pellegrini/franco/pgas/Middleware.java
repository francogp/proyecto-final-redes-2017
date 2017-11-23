package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {

    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws IOException;

    void setDebugMode( boolean mode );

    void startServer();

    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException;
}
