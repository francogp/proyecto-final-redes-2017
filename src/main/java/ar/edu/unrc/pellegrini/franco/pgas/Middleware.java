package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;

public
interface Middleware< I extends Comparable< I > > {

    HostConfig< I > getHostByPid( long pid );

    void receiveFrom( final long pid );

    void sendTo(
            final long pid,
            final I value
    );

}

