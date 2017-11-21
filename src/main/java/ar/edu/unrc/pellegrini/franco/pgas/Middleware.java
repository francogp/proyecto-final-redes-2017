package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.READ_RESPONSE_MSG;

public abstract
class Middleware< I extends Comparable< I > > {
    public static final boolean DEBUG_MODE = true;
    private final Configs< I >      configs;
    private final HostConfig< I >   hostsConfig;
    private final AbstractPGAS< I > pgas;
    private final Server< I >       server;
    private final Thread            serverThread;

    public
    Middleware(
            final AbstractPGAS< I > pgas,
            final Configs< I > configs
    ) {
        this(pgas, configs, true);
    }

    public
    Middleware(
            final AbstractPGAS< I > pgas,
            final Configs< I > configs,
            final boolean starServer
    ) {
        this.pgas = pgas;
        this.configs = configs;
        final int pid = pgas.getPid();
        hostsConfig = configs.getHostsConfig(pid);
        final int port = hostsConfig.getPort();
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
            serverThread = null;
        }
    }

    /**
     * @param inetAddress
     * @param port
     * @param msgtype
     * @param parameter1  cannot be null
     * @param parameter2  cannot be null
     *
     * @return
     */
    protected abstract
    Message< I > newMessageInstanceFrom(
            final InetAddress inetAddress,
            final int port,
            final MessageType msgtype,
            final I parameter1,
            final I parameter2
    );

    protected abstract
    Server< I > newServer( int port )
            throws SocketException;

    protected
    void processIncomingMessage( final Message< I > incomingMessage )
            throws IOException, InterruptedException {
        //        public static final char AND_REDUCE_MSG         = 'A';
        //        public static final char BARRIER_MSG            = 'B';
        //        public static final char CONTINUE_MSG           = 'C';
        //        public static final char END_MSG                = 'E';
        //        public static final char READ_MSG               = 'R';
        //        public static final char WRITE_MSG              = 'W';
        //TODO buscar asi el pid o mandar por mensaje?
        final HostConfig< I > targetHost = configs.getHostsConfig(incomingMessage.getAddress(), incomingMessage.getPort());
        switch ( incomingMessage.getType() ) {
            case AND_REDUCE_MSG: {
                assert pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case BARRIER_MSG: {
                assert pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case CONTINUE_MSG: {
                assert !pgas.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case END_MSG: {
                break;
            }
            case READ_MSG: {
                sendTo(targetHost, READ_RESPONSE_MSG, pgas.read((Long) incomingMessage.getParameter1()), null);
                break;
            }
            case READ_RESPONSE_MSG: {
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case WRITE_MSG: {
                pgas.write((Long) incomingMessage.getParameter1(), incomingMessage.getParameter2());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Message<I> type = " + incomingMessage.getType());
        }
    }

    public
    void sendTo(
            final HostConfig< I > targetHost,
            final MessageType msgType,
            final I parameter1,
            final I parameter2
    )
            throws IOException {
        final Message< I > msg = newMessageInstanceFrom(targetHost.getInetAddress(), targetHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(pgas.getPid() + " sendTo( pid=" + configs.getHostsConfig(targetHost.getInetAddress(), targetHost.getPort()).getPid() +
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
        final HostConfig< I > destHost = configs.getHostsConfig(targetPid);
        final Message< I >    msg      = newMessageInstanceFrom(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(
                    pgas.getPid() + " sendTo( pid=" + targetPid + " ) type=" + msgType + " param1=" + parameter1 + " param2=" + parameter2);
        }
        server.send(msg);
    }

    public
    Message< I > waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws IOException, InterruptedException {
        final HostConfig< I > hostsConfig = configs.getHostsConfig(senderPid);
        if ( DEBUG_MODE ) {
            System.out.println(pgas.getPid() + " waitFor( pid=" + senderPid + " ) type=" + msgType);
        }
        return hostsConfig.waitFor(msgType);
    }


}

