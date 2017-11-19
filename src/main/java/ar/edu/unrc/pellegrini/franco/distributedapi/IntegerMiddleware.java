package ar.edu.unrc.pellegrini.franco.distributedapi;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.util.Map;

public
class IntegerMiddleware
        implements Middleware< Integer > {
    private final Map< Long, String > hosts;
    private final PGAS< Integer >     integerPGAS;

    public
    IntegerMiddleware(
            final PGAS< Integer > integerPGAS,
            final Configs configs
    ) {
        this.integerPGAS = integerPGAS;
        this.hosts = configs.getHosts();
    }

    public
    String getHostByPid( long pid ) {
        return hosts.get(pid);
    }

    @Override
    public
    void receiveFrom( final long pid ) {

    }

    @Override
    public
    void sendTo(
            final long pid,
            final Integer value
    ) {

    }

}
