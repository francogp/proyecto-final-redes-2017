package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.MessageType;
import ar.edu.unrc.pellegrini.franco.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Host;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.READ_RESPONSE_MSG;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public abstract
class AbstractMiddleware< I extends Comparable< I > >
        implements Middleware< I > {

    private final NetConfiguration< I > netConfiguration;
    private final PGAS< I >             pgas;
    private final Server< I >           server;

    protected
    AbstractMiddleware(
            final PGAS< I > pgas,
            final NetConfiguration< I > netConfiguration
    ) {
        this(pgas, netConfiguration, true);
    }

    protected
    AbstractMiddleware(
            final PGAS< I > pgas,
            final NetConfiguration< I > netConfiguration,
            final boolean starServer
    ) {
        this.pgas = pgas;
        this.netConfiguration = netConfiguration;
        final int       pid         = pgas.getPid();
        final Host< I > hostsConfig = netConfiguration.getHostsConfig(pid);
        final int       port        = hostsConfig.getPort();
        final Thread    serverThread;
        if ( starServer ) {
            try {
                server = newServer(port);
                serverThread = new Thread(server);
                serverThread.start();
            } catch ( final SocketException e ) {
                throw new IllegalArgumentException("Cannot bind the port " + port + " to localhost pid " + pid, e);
            }
        } else {
            server = null;
        }
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
            final Long indexParameter,
            final I valueParameter
    );

    protected abstract
    Server< I > newServer( int port )
            throws SocketException;

    protected final
    void processIncomingMessage( final Message< I > incomingMessage )
            throws IOException, InterruptedException {
        //TODO buscar asi el pid o mandar por mensaje?
        final Host< I > targetHost = netConfiguration.getHostsConfig(incomingMessage.getAddress(), incomingMessage.getPort());
        switch ( incomingMessage.getType() ) {
            case AND_REDUCE_MSG:
                assert pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            case BARRIER_MSG:
                assert pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            case CONTINUE_MSG:
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

    public final
    void sendTo(
            final Host< I > targetHost,
            final MessageType msgType,
            final Long indexParameter,
            final I valueParameter
    )
            throws IOException {
        final Message< I > msg = newMessageInstanceFrom(targetHost.getInetAddress(), targetHost.getPort(), msgType, indexParameter, valueParameter);
        if ( DEBUG_MODE ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pgas.getPid())
                    .append("] -> sendTo pid[")
                    .append(netConfiguration.getHostsConfig(targetHost.getInetAddress(), targetHost.getPort()).getPid())
                    .append("] ")
                    .append(msgType)
                    .append(( indexParameter != null ) ? " param1=" + indexParameter : "")
                    .append(( valueParameter != null ) ? " param2=" + valueParameter : "")
                    .toString());
        }
        server.send(msg);
    }

    public final
    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final Long indexParameter,
            final I valueParameter
    )
            throws IOException {
        final Host< I >    destHost = netConfiguration.getHostsConfig(targetPid);
        final Message< I > msg      = newMessageInstanceFrom(destHost.getInetAddress(), destHost.getPort(), msgType, indexParameter, valueParameter);
        if ( DEBUG_MODE ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pgas.getPid())
                    .append("] -> sendTo pid[")
                    .append(targetPid)
                    .append("] ")
                    .append(msgType)
                    .append(( indexParameter != null ) ? " param1=" + indexParameter : "")
                    .append(( valueParameter != null ) ? " param2=" + valueParameter : "")
                    .toString());
        }
        server.send(msg);
    }

    public final
    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException {
        final Host< I > hostsConfig = netConfiguration.getHostsConfig(senderPid);
        if ( DEBUG_MODE ) {
            System.out.println(new StringBuilder().append("Time ")
                    .append(System.nanoTime())
                    .append(": pid[")
                    .append(pgas.getPid())
                    .append("] -> waitFor pid[")
                    .append(senderPid)
                    .append("] ")
                    .append(msgType)
                    .toString());
        }
        return hostsConfig.waitFor(msgType);
    }


}

