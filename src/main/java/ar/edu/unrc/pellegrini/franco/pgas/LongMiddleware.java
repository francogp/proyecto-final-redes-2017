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
            final String msg
    )
            throws IOException {
        final Configs.HostConfig< Long > destHost = configs.getHostsConfig(pid);
        server.sendTo(destHost.getInetAddress(), destHost.getPort(), msg);
    }

    @Override
    public
    Message waitFor(
            final int pid,
            final String msg
    )
            throws IOException {
        final Configs.HostConfig< Long > destHost = configs.getHostsConfig(pid);
        server.sendTo(destHost.getInetAddress(), destHost.getPort(), msg);
        return null;
    }

}
