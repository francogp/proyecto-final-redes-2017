package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;

import java.net.InetAddress;
import java.util.List;

public
interface Process< I > {
    InetAddress getInetAddress();

    int getPid();

    Integer getPort();

    List< I > getValues( int pgasName );

    void registerMsg( Message< I > message )
            throws InterruptedException;

    Message< I > waitFor(
            int pgasName,
            MessageType msgType
    )
            throws InterruptedException;
}
