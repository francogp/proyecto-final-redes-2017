package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.pgas.net.Message;

import java.io.IOException;

public
interface Middleware< I extends Comparable< I > > {
    String BARRIER_MSG  = "B";
    String CONTINUE_MSG = "C";
    String READ_MSG     = "R";
    String RESULT_MSG   = "S";
    String WRITE_MSG    = "W";

    void receiveFrom( final int pid );

    void sendTo(
            final int pid,
            final String msg
    )
            throws IOException;

    Message waitFor(
            final int pid,
            final String msg
    )
            throws IOException;

}

