package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.READ_RESPONSE_MSG;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractMiddleware< I extends Comparable< I > >
        implements Middleware< I > {

    protected static boolean debugMode = false;
    private final NetConfiguration< I > netConfiguration;
    private final PGAS< I >             pgas;
    private final int                   port;
    private       Server< I >           server;

    protected
    AbstractMiddleware(
            final PGAS< I > pgas,
            final NetConfiguration< I > netConfiguration
    ) {
        this.pgas = pgas;
        this.netConfiguration = netConfiguration;
        final Host< I > hostsConfig = netConfiguration.getHostsConfig(pgas.getPid());
        port = hostsConfig.getPort();
        server = null;
    }

    /**
     * @param inetAddress
     * @param port
     * @param messageType
     * @param indexParameter cannot be null
     * @param valueParameter cannot be null
     *
     * @return
     */
    protected abstract
    Message< I > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType messageType,
            final long indexParameter,
            final I valueParameter
    );

    protected abstract
    Server< I > newServer( int port )
            throws SocketException;

    protected final
    void processIncomingMessage( final Message< I > incomingMessage )
            throws IOException, InterruptedException {
        final Host< I > targetHost = netConfiguration.getHostsConfig(incomingMessage.getAddress(), incomingMessage.getPort());
        switch ( incomingMessage.getType() ) {
            case AND_REDUCE_MSG:
            case BARRIER_MSG:
                assert pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            case CONTINUE_BARRIER_MSG:
            case CONTINUE_AND_REDUCE_MSG:
                assert !pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            case END_MSG:
                break;
            case READ_MSG:
                //FIXME synchronized causa problemas aca
                sendTo(targetHost, READ_RESPONSE_MSG, incomingMessage.getIndexParameter(), pgas.read(incomingMessage.getIndexParameter()));
                break;
            case READ_RESPONSE_MSG:
                targetHost.registerMsg(incomingMessage);
                break;
            case WRITE_MSG:
                //FIXME synchronized causa problemas aca
                pgas.write(incomingMessage.getIndexParameter(), incomingMessage.getValueParameter());
                break;
            default:
                throw new IllegalArgumentException("Unknown Message<I> type = " + incomingMessage.getType());
        }
    }

    private
    void sendTo(
            final Host< I > targetHost,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws IOException {
        if ( server == null ) {
            throw new IllegalStateException("Server not started. Use startServer()");
        }
        //FIXME pasar mensje como parametro y no instanciarlo aca
        final Message< I > msg = newMessageInstanceFrom(targetHost.getInetAddress(), targetHost.getPort(), msgType, indexParameter, valueParameter);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pgas.getPid())
                    .append("] -> sendTo pid[")
                    .append(netConfiguration.getHostsConfig(targetHost.getInetAddress(), targetHost.getPort()).getPid())
                    .append("] ")
                    .append(msgType)
                    .append(" { index=")
                    .append(indexParameter)
                    .append(( valueParameter != null ) ? ( ", value=" + valueParameter ) : "")
                    .append(" }"));
        }
        server.send(msg);
    }

    @Override
    public final
    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long indexParameter,
            final I valueParameter
    )
            throws IOException {
        sendTo(netConfiguration.getHostsConfig(targetPid), msgType, indexParameter, valueParameter);
    }

    @Override
    public final
    void setDebugMode( final boolean mode ) {
        debugMode = mode;
    }

    @Override
    public final
    void startServer() {
        try {
            server = newServer(port);
            final Thread serverThread = new Thread(server);
            serverThread.start();
        } catch ( final SocketException e ) {
            throw new IllegalArgumentException("Cannot bind the port " + port + " to localhost pid " + pgas.getPid(), e);
        }
    }

    @Override
    public final
    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException {
        final Host< I > hostsConfig = netConfiguration.getHostsConfig(senderPid);
        if ( debugMode ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pgas.getPid())
                    .append("] -> waitFor pid[")
                    .append(senderPid)
                    .append("] ")
                    .append(msgType));
        }
        return hostsConfig.waitFor(msgType);
    }


}

