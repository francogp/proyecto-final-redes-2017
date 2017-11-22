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
class Middleware< I extends Comparable< I > > {
    public static final boolean DEBUG_MODE = true;
    private final NetConfiguration< I > netConfiguration;
    private final AbstractPGAS< I >     pgas;
    private final Server< I >           server;

    protected
    Middleware(
            final AbstractPGAS< I > pgas,
            final NetConfiguration< I > netConfiguration
    ) {
        this(pgas, netConfiguration, true);
    }

    protected
    Middleware(
            final AbstractPGAS< I > pgas,
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
     * @param parameter1  cannot be null
     * @param parameter2  cannot be null
     *
     * @return
     */
    protected abstract
    Message< I > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType messageType,
            final I parameter1,
            final I parameter2
    );

    protected abstract
    Server< I > newServer( int port )
            throws SocketException;

    protected
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
                sendTo(targetHost, READ_RESPONSE_MSG, pgas.read((Long) incomingMessage.getParameter1()), null);
                break;
            case READ_RESPONSE_MSG:
                targetHost.registerMsg(incomingMessage);
                break;
            case WRITE_MSG:
                //FIXME synchronized causa problemas aca
                pgas.write((Long) incomingMessage.getParameter1(), incomingMessage.getParameter2());
                break;
            default:
                throw new IllegalArgumentException("Unknown Message<I> type = " + incomingMessage.getType());
        }
    }

    public
    void sendTo(
            final Host< I > targetHost,
            final MessageType msgType,
            final I parameter1,
            final I parameter2
    )
            throws IOException {
        final Message< I > msg = newMessageInstanceFrom(targetHost.getInetAddress(), targetHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(
                    pgas.getPid() + " sendTo( pid=" + netConfiguration.getHostsConfig(targetHost.getInetAddress(), targetHost.getPort()).getPid() +
                    " ) type=" + msgType + " param1=" + parameter1 + " param2=" + parameter2);
        }
        server.send(msg);
    }

    public
    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final I parameter1,
            final I parameter2
    )
            throws IOException {
        final Host< I >    destHost = netConfiguration.getHostsConfig(targetPid);
        final Message< I > msg      = newMessageInstanceFrom(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(pgas.getPid() + " sendTo( pid=" + targetPid + " ) type=" + msgType + " param1=" + parameter1 + " param2=" + parameter2);
        }
        server.send(msg);
    }

    public
    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws InterruptedException {
        final Host< I > hostsConfig = netConfiguration.getHostsConfig(senderPid);
        if ( DEBUG_MODE ) {
            System.out.println(pgas.getPid() + " waitFor( pid=" + senderPid + " ) type=" + msgType);
        }
        return hostsConfig.waitFor(msgType);
    }


}

