package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.Server;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessageServer;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractMiddleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMiddleware
        extends AbstractMiddleware< Long > {

    public
    LongMiddleware(
            final PGAS< Long > pgas,
            final NetConfiguration< Long > netConfiguration
    ) {
        super(pgas, netConfiguration);
    }

    @Override
    protected
    Message< Long > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType messageType,
            final Long indexParameter,
            final Long valueParameter
    ) {
        return new LongMessage(inetAddress, port, messageType, indexParameter, valueParameter);
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
