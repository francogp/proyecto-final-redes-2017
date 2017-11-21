package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

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
            final int pid,
            final String configsFilePath
    ) {
        super(pid, configsFilePath);
    }

    public
    LongPGAS(
            final int pid,
            final String configsFilePath,
            final boolean startServer
    ) {
        super(pid, configsFilePath, startServer);
    }

    public
    LongPGAS(
            final int pid,
            final File configsFile
    ) {
        super(pid, configsFile);
    }

    public
    LongPGAS(
            final int pid,
            final File configsFile,
            final boolean startServer
    ) {
        super(pid, configsFile, startServer);
    }

    public
    String asString() {
        return LongStream.range(0L, pgasSize).mapToObj(index -> {
            try {
                return Long.toString(read(index));
            } catch ( Exception e ) {
                getLogger(LongPGAS.class.getName()).log(Level.SEVERE, null, e);
                return "ERROR";
            }
        }).collect(Collectors.joining(","));
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
    Long indexAsMessageParameter( final Long index ) {
        return index;
    }

    @Override
    protected
    Middleware< Long > newMiddleware(
            final boolean startServer,
            final Configs< Long > configFile
    ) {
        return new LongMiddleware(this, configFile, startServer);
    }

    /**
     * @param msg
     *
     * @return true!=0, false==0
     */
    @Override
    protected
    boolean responseToBooleanRepresentation( final Message< Long > msg ) {
        return msg.getResponse() != 0L;
    }
}
