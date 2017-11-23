package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;
import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;

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
            int pid,
            String configsFilePath
    ) {
        super(pid, configsFilePath);
    }

    public
    DoublePGAS(
            int pid,
            File configsFile
    ) {
        super(pid, configsFile);
    }

    public
    DoublePGAS(
            int pid,
            NetConfiguration< Double > configFile
    ) {
        super(pid, configFile);
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
            final NetConfiguration< Double > configFile
    ) {
        return new DoubleMiddleware(this, configFile);
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
