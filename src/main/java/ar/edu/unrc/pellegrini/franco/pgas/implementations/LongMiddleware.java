package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.pgas.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.pgas.net.implementations.LongMessageServer;
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
            AbstractPGAS< Long > pgas,
            Configs< Long > configs
    ) {
        super(pgas, configs);
    }

    public
    LongMiddleware(
            AbstractPGAS< Long > pgas,
            Configs< Long > configs,
            boolean starServer
    ) {
        super(pgas, configs, starServer);
    }

    @Override
    protected
    Message< Long > newMessageInstanceFrom(
            InetAddress inetAddress,
            int port,
            MessageType msgtype,
            Long parameter1,
            Long parameter2
    ) {
        return new LongMessage(inetAddress, port, msgtype, parameter1, parameter2);
    }

    @Override
    protected
    Server< Long > newServer( int port )
            throws SocketException {
        return new LongMessageServer(port, ( msg ) -> {
            try {
                processIncomingMessage(msg);
            } catch ( Exception e ) {
                getLogger(LongMiddleware.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }
}
