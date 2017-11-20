package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;

import java.io.IOException;
import java.net.SocketException;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongMiddleware
        implements Middleware< Long > {
    private final Configs< Long > configs;
    private final PGAS< Long >    longPGAS;
    private final Server          server;
    private final Thread          serverThread;

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
        final int pid  = longPGAS.getPid();
        final int port = configs.getHostsConfig(pid).getPort();
        if ( starServer ) {
            try {
                server = new Server(port);
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

    @Override
    public
    void receiveFrom( final int pid ) {

    }

    @Override
    public
    void sendTo(
            final int pid,
            final char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final HostConfig< Long > destHost = configs.getHostsConfig(pid);
        final Message            msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            final int pid,
            final char msgType,
            final long parameter1
    )
            throws IOException {
        sendTo(pid, msgType, parameter1, 0L);
    }

    @Override
    public
    void sendTo(
            final int pid,
            final char msgType
    )
            throws IOException {
        sendTo(pid, msgType, 0L, 0L);
    }

    @Override
    public
    Message waitFor(
            final int pid,
            final char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final HostConfig< Long > destHost = configs.getHostsConfig(pid);
        final Message            msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
        //TODO completar!
        return null;
    }

    @Override
    public
    Message waitFor(
            final int pid,
            final char msgType,
            final long parameter1
    )
            throws IOException {
        waitFor(pid, msgType, parameter1, 0L);
        //TODO completar!
        return null;
    }

    @Override
    public
    Message waitFor(
            final int pid,
            final char msgType
    )
            throws IOException {
        waitFor(pid, msgType, 0L, 0L);
        //TODO completar!
        return null;
    }

}
