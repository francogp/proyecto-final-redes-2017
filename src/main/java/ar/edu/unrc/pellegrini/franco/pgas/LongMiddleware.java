package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.MessageType;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.READ_RESPONSE_MSG;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMiddleware
        implements Middleware< Long > {
    public static final boolean DEBUG_MODE = true;
    private final Configs< Long >    configs;
    private final HostConfig< Long > hostsConfig;
    private final PGAS< Long >       longPGAS;
    private final Server             server;
    private final Thread             serverThread;

    public
    LongMiddleware(
            final PGAS< Long > longPGAS,
            final Configs< Long > configs
    ) {
        this(longPGAS, configs, true);
    }

    public
    LongMiddleware(
            final PGAS< Long > longPGAS,
            final Configs< Long > configs,
            final boolean starServer
    ) {
        this.longPGAS = longPGAS;
        this.configs = configs;
        final int pid = longPGAS.getPid();
        hostsConfig = configs.getHostsConfig(pid);
        final int port = hostsConfig.getPort();
        if ( starServer ) {
            try {
                server = new Server(port, ( msg ) -> {
                    try {
                        processIncomingMessage(msg);
                    } catch ( Exception e ) {
                        getLogger(LongMiddleware.class.getName()).log(Level.SEVERE, null, e);
                    }
                });
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

    private
    void processIncomingMessage( final Message incomingMessage )
            throws IOException, InterruptedException {
        //        public static final char AND_REDUCE_MSG         = 'A';
        //        public static final char BARRIER_MSG            = 'B';
        //        public static final char CONTINUE_MSG           = 'C';
        //        public static final char END_MSG                = 'E';
        //        public static final char READ_MSG               = 'R';
        //        public static final char WRITE_MSG              = 'W';
        //TODO buscar asi el pid o mandar por mensaje?
        final HostConfig< Long > targetHost = configs.getHostsConfig(incomingMessage.getAddress(), incomingMessage.getPort());
        switch ( incomingMessage.getType() ) {
            case AND_REDUCE_MSG: {
                assert longPGAS.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case BARRIER_MSG: {
                assert longPGAS.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case CONTINUE_MSG: {
                assert !longPGAS.isCoordinator();
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case END_MSG: {
                break;
            }
            case READ_MSG: {
                sendTo(targetHost, READ_RESPONSE_MSG, longPGAS.read(incomingMessage.getParameter1()));
                break;
            }
            case READ_RESPONSE_MSG: {
                targetHost.registerMsg(incomingMessage);
                break;
            }
            case WRITE_MSG: {
                longPGAS.write(incomingMessage.getParameter1(), incomingMessage.getParameter2());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown message type = " + incomingMessage.getType());
        }
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final Message msg = new Message(targetHost.getInetAddress(), targetHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(
                    longPGAS.getPid() + " sendTo( pid=" + configs.getHostsConfig(targetHost.getInetAddress(), targetHost.getPort()).getPid() +
                    " ) type=" + msgType + " param1=" + parameter1 + " param2=" + parameter2);
        }
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final HostConfig< Long > destHost = configs.getHostsConfig(targetPid);
        final Message            msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        if ( DEBUG_MODE ) {
            System.out.println(
                    longPGAS.getPid() + " sendTo( pid=" + targetPid + " ) type=" + msgType + " param1=" + parameter1 + " param2=" + parameter2);
        }
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType
    )
            throws IOException {
        sendTo(targetHost, msgType, 0L, 0L);
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final MessageType msgType,
            final long parameter1
    )
            throws IOException {
        sendTo(targetHost, msgType, parameter1, 0L);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final MessageType msgType,
            final long parameter1
    )
            throws IOException {
        sendTo(targetPid, msgType, parameter1, 0L);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final MessageType msgType
    )
            throws IOException {
        sendTo(targetPid, msgType, 0L, 0L);
    }

    @Override
    public
    Message waitFor(
            final int senderPid,
            final MessageType msgType
    )
            throws IOException, InterruptedException {
        final HostConfig< Long > hostsConfig = configs.getHostsConfig(senderPid);
        if ( DEBUG_MODE ) {
            System.out.println(longPGAS.getPid() + " waitFor( pid=" + senderPid + " ) type=" + msgType);
        }
        return hostsConfig.waitFor(msgType);
    }

}
