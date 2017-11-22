package ar.edu.unrc.pellegrini.franco.net;

import java.io.IOException;
import java.nio.charset.Charset;

public
interface Server< I extends Comparable< I > >
        extends Runnable {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    boolean isRunning();

    void send( final Message< I > msg )
            throws IOException;
}
