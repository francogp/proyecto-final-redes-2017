package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

public
class LongMiddleware
        implements Middleware< Long > {
    private final PGAS< Long > longPGAS;
    private       Configs      configs;

    public
    LongMiddleware(
            final PGAS< Long > longPGAS,
            final Configs configs
    ) {
        this.longPGAS = longPGAS;
        this.configs = configs;
    }

    public
    Configs.HostConfig getHostByPid( long pid ) {
        return configs.getHostsConfig(pid);
    }

    @Override
    public
    void receiveFrom( final long pid ) {

    }

    @Override
    public
    void sendTo(
            final long pid,
            final Long value
    ) {

    }

}
