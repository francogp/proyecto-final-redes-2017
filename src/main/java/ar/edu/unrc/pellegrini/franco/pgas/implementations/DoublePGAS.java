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
class DoublePGAS
        extends AbstractPGAS< Double > {

    public
    DoublePGAS(
            final int pid,
            final String configsFilePath
    ) {
        super(pid, configsFilePath);
    }

    public
    DoublePGAS(
            final int pid,
            final String configsFilePath,
            final boolean startServer
    ) {
        super(pid, configsFilePath, startServer);
    }

    public
    DoublePGAS(
            final int pid,
            final File configsFile
    ) {
        super(pid, configsFile);
    }

    public
    DoublePGAS(
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
                return Double.toString(read(index));
            } catch ( Exception e ) {
                getLogger(DoublePGAS.class.getName()).log(Level.SEVERE, null, e);
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
    Double booleanAsMessageParameter( final boolean value ) {
        return ( value ) ? 1.0d : 0.0d;
    }

    @Override
    protected
    Middleware< Double > newMiddleware(
            final boolean startServer,
            final NetConfiguration< Double > configFile
    ) {
        return new DoubleMiddleware(this, configFile, startServer);
    }

    /**
     * @param message
     *
     * @return true!=0, false==0
     */
    @Override
    protected
    boolean parseResponseAsBoolean( final Message< Double > message ) {
        return message.getValueParameter() != 0.0d;
    }
}
