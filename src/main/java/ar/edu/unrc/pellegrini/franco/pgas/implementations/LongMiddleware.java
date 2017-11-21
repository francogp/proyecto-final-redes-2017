package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.Server;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessageServer;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMiddleware
        extends Middleware< Long > {


    public
    LongMiddleware(
            final AbstractPGAS< Long > pgas,
            final Configs< Long > configs
    ) {
        super(pgas, configs);
    }

    public
    LongMiddleware(
            final AbstractPGAS< Long > pgas,
            final Configs< Long > configs,
            final boolean starServer
    ) {
        super(pgas, configs, starServer);
    }

    @Override
    protected
    Message< Long > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType messageType,
            final Long parameter1,
            final Long parameter2
    ) {
        return new LongMessage(inetAddress, port, messageType, parameter1, parameter2);
    }

    @Override
    protected
    Server< Long > newServer( final int port )
            throws SocketException {
        return new LongMessageServer(port, ( msg ) -> {
            try {
                processIncomingMessage(msg);
            } catch ( final Exception e ) {
                getLogger(LongMiddleware.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }
}
