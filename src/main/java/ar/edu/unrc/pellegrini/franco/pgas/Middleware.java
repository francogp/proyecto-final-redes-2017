package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.io.IOException;

public
interface Middleware< I > {

    boolean andReduce( final boolean value )
            throws Exception;

    void barrier()
            throws Exception;

    void closeListener()
            throws Exception;

    Process< I > getProcessConfigugation( final int pid );

    int getProcessQuantity();

    int getWhoAmI();

    boolean imLast();

    boolean isCoordinator();

    void registerPGAS( final PGAS< I > pgas );

    void sendMessage(
            final Message< I > msg
    )
            throws IOException;

    void sendTo(
            final int pgasName,
            final int targetPid,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws Exception;

    void sendTo(
            final int pgasName,
            final Process< I > targetProcess,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws Exception;

    void setDebugMode( final boolean mode );

    void startServer();

    Message< I > waitFor( //FIXME cambiar nombre a recieveFrom
            final int pgasName,
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException;
}
