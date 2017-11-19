package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;

public
interface Middleware< I > {

    Configs.HostConfig getHostByPid( long pid );

    void receiveFrom( final long pid );

    void sendTo(
            final long pid,
            final I value
    );

}

