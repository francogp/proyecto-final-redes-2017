package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import java.io.File;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.logging.Logger.getLogger;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class LongPGAS
        extends AbstractPGAS< Long > {
    public
    LongPGAS(
            int pid,
            String configsFilePath
    ) {
        super(pid, configsFilePath);
    }

    public
    LongPGAS(
            int pid,
            File configsFile
    ) {
        super(pid, configsFile);
    }

    public
    LongPGAS(
            int pid,
            NetConfiguration< Long > configFile
    ) {
        super(pid, configFile);
    }

    @Override
    public
    String asString() {
        return LongStream.range(0L, pgasSize).mapToObj(index -> {
            try {
                return Long.toString(read(index));
            } catch ( Exception e ) {
                getLogger(LongPGAS.class.getName()).log(Level.SEVERE, null, e);
                return "ERROR";
            }
        }).collect(Collectors.joining(", "));
    }

    /**
     * @param value
     *
     * @return true!=0, false==0
     */
    @Override
    protected
    Long booleanAsMessageParameter( final boolean value ) {
        return ( value ) ? 1L : 0L;
    }

    @Override
    protected
    Middleware< Long > newMiddleware(
            final NetConfiguration< Long > configFile
    ) {
        return new LongMiddleware(this, configFile);
    }

    /**
     * @param message
     *
     * @return true!=0, false==0
     */
    @Override
    protected
    boolean parseResponseAsBoolean( final Message< Long > message ) {
        return message.getValueParameter() != 0L;
    }
}
