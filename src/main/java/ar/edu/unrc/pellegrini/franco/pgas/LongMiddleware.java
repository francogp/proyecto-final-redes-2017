package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.net.Server;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.io.IOException;
import java.net.SocketException;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
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
        int pid  = longPGAS.getPid();
        int port = configs.getHostsConfig(pid).getPort();
        if ( starServer ) {
            try {
                server = new Server(port);
                serverThread = new Thread(server);
                serverThread.start();
            } catch ( SocketException e ) {
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
            char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final Configs.HostConfig< Long > destHost = configs.getHostsConfig(pid);
        Message                          msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
    }

    @Override
    public
    void sendTo(
            int pid,
            char msgType,
            long parameter1
    )
            throws IOException {
        sendTo(pid, msgType, parameter1, 0);
    }

    @Override
    public
    void sendTo(
            int pid,
            char msgType
    )
            throws IOException {
        sendTo(pid, msgType, 0, 0);
    }

    @Override
    public
    Message waitFor(
            final int pid,
            char msgType,
            final long parameter1,
            final long parameter2
    )
            throws IOException {
        final Configs.HostConfig< Long > destHost = configs.getHostsConfig(pid);
        Message                          msg      = new Message(destHost.getInetAddress(), destHost.getPort(), msgType, parameter1, parameter2);
        server.send(msg);
        //TODO completar!
        return null;
    }

    @Override
    public
    Message waitFor(
            int pid,
            char msgType,
            long parameter1
    )
            throws IOException {
        waitFor(pid, msgType, parameter1, 0);
        //TODO completar!
        return null;
    }

    @Override
    public
    Message waitFor(
            int pid,
            char msgType
    )
            throws IOException {
        waitFor(pid, msgType, 0, 0);
        //TODO completar!
        return null;
    }

}
