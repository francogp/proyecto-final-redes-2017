package ar.edu.unrc.pellegrini.franco.net;

import java.io.IOException;

public
interface Server< I extends Comparable< I > >
        extends Runnable {

    boolean isRunning();

    void send( final Message< I > msg )
            throws IOException;
}
