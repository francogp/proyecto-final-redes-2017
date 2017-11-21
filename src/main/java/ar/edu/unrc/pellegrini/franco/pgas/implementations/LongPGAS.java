package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.AbstractPGAS;
import ar.edu.unrc.pellegrini.franco.pgas.Middleware;
import ar.edu.unrc.pellegrini.franco.pgas.net.Message;
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
            int pid,
            String configsFilePath
    ) {
        super(pid, configsFilePath);
    }

    public
    LongPGAS(
            int pid,
            String configsFilePath,
            boolean startServer
    ) {
        super(pid, configsFilePath, startServer);
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
            File configsFile,
            boolean startServer
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

    @Override
    protected
    Middleware< Long > newMiddleware(
            boolean startServer,
            Configs< Long > configFile
    ) {
        return new LongMiddleware(this, configFile, startServer);
    }

    /**
     * true!=0, false==0
     *
     * @param bool
     *
     * @return
     */
    @Override
    protected
    boolean responseAsBooleanRepresentation( Message< Long > msg ) {
        return msg.getResponse() != 0;
    }
}
