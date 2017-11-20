package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;
import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public
class LongMiddleware
        implements Middleware< Long > {
    private final Configs< Long > configs;
    private final PGAS< Long >    longPGAS;

    public
    LongMiddleware(
            final PGAS< Long > longPGAS,
            final Configs< Long > configs
    ) {
        this.longPGAS = longPGAS;
        this.configs = configs;
    }

    public
    HostConfig< Long > getHostByPid( final long pid ) {
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
