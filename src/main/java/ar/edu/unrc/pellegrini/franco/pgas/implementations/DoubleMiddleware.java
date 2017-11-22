package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.Server;
import ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessageServer;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractMiddleware;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DoubleMiddleware
        extends AbstractMiddleware< Double > {


    public
    DoubleMiddleware(
            final PGAS< Double > pgas,
            final NetConfiguration< Double > netConfiguration
    ) {
        super(pgas, netConfiguration);
    }

    public
    DoubleMiddleware(
            final PGAS< Double > pgas,
            final NetConfiguration< Double > netConfiguration,
            final boolean starServer
    ) {
        super(pgas, netConfiguration, starServer);
    }

    @Override
    protected
    Message< Double > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType messageType,
            final Long indexParameter,
            final Double valueParameter
    ) {
        return new DoubleMessage(inetAddress, port, messageType, indexParameter, valueParameter);
    }

    @Override
    protected
    Server< Double > newServer( final int port )
            throws SocketException {
        return new DoubleMessageServer(port, ( msg ) -> {
            try {
                processIncomingMessage(msg);
            } catch ( final Exception e ) {
                getLogger(DoubleMiddleware.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }
}
