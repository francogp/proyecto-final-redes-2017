package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.HostConfig;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;

import static ar.edu.unrc.pellegrini.franco.pgas.net.Message.*;
import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMiddleware
        implements Middleware< Long > {
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
                        processIncommingMessage(msg);
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
    void processIncommingMessage( final Message incommingMessage )
            throws IOException, InterruptedException {
        //        public static final char AND_REDUCE_MSG         = 'A';
        //        public static final char BARRIER_MSG            = 'B';
        //        public static final char CONTINUE_MSG           = 'C';
        //        public static final char END_MSG                = 'E';
        //        public static final char READ_MSG               = 'R';
        //        public static final char WRITE_MSG              = 'W';
        final HostConfig< Long > targetHost = configs.getHostsConfig(incommingMessage.getAddress(), incommingMessage.getPort());
        switch ( incommingMessage.getType() ) {
            case AND_REDUCE_MSG: {
                assert longPGAS.isCoordinator();
                targetHost.registerMsg(incommingMessage);
                break;
            }
            case BARRIER_MSG: {
                assert longPGAS.isCoordinator();
                targetHost.registerMsg(incommingMessage);
                break;
            }
            case CONTINUE_MSG: {
                assert !longPGAS.isCoordinator();
                targetHost.registerMsg(incommingMessage);
                break;
            }
            case END_MSG: {
                break;
            }
            case READ_MSG: {
                sendTo(targetHost, READ_RESPONSE_MSG, longPGAS.read(incommingMessage.getParameter1()));
                break;
            }
            case READ_RESPONSE_MSG: {
                targetHost.registerMsg(incommingMessage);
                break;
            }
            case WRITE_MSG: {
                longPGAS.write(incommingMessage.getParameter1(), incommingMessage.getParameter2());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown message type = " + incommingMessage.getType());
        }
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final Message msg = new Message(targetHost.getInetAddress(), targetHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final HostConfig< Long > destHost = configs.getHostsConfig(targetPid);
        final Message            msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType
    )
            throws IOException {
        sendTo(targetHost, msgType, 0L, 0L);
    }

    @Override
    public
    void sendTo(
            final HostConfig< Long > targetHost,
            final char msgType,
            final long parameter1
    )
            throws IOException {
        sendTo(targetHost, msgType, parameter1, 0L);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final char msgType,
            final long parameter1
    )
            throws IOException {
        sendTo(targetPid, msgType, parameter1, 0L);
    }

    @Override
    public
    void sendTo(
            final int targetPid,
            final char msgType
    )
            throws IOException {
        sendTo(targetPid, msgType, 0L, 0L);
    }

    @Override
    public
    Message waitFor(
            final int senderPid,
            final char msgType
    )
            throws IOException, InterruptedException {
        final HostConfig< Long > hostsConfig = configs.getHostsConfig(senderPid);
        return hostsConfig.waitFor(msgType);
    }

}
