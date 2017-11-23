package ar.edu.unrc.pellegrini.franco.bubblesort.implementations;

import ar.edu.unrc.pellegrini.franco.bubblesort.AbstractDistributedBubbleSort;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.DoublePGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedBubbleSortForDouble
        extends AbstractDistributedBubbleSort< Double > {
    public
    DistributedBubbleSortForDouble(
            final int pid,
            final String configFilePath,
            final boolean debugMode
    ) {
        super(pid, configFilePath, debugMode);
    }

    public
    DistributedBubbleSortForDouble(
            final int pid,
            final NetConfiguration< Double > configFilePath,
            final boolean debugMode
    ) {
        super(pid, configFilePath, debugMode);
    }

    public static
    void main( final String... args ) {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_PID);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.addValidFlag(ARG_DEBUG_MODE);

        arguments.loadArguments(args);
        final int pid = arguments.parseInteger(ARG_PID);
        final Runnable bubbleSort =
                new DistributedBubbleSortForDouble(pid, arguments.parseString(ARG_CONFIG_FILE), arguments.existsFlag(ARG_DEBUG_MODE));
        bubbleSort.run();
    }

    @Override
    protected
    PGAS< Double > newPGAS(
            final int pid,
            final NetConfiguration< Double > configFilePath,
            final boolean debugMode
    ) {
        final DoublePGAS doublePGAS = new DoublePGAS(pid, configFilePath);
        doublePGAS.setDebugMode(debugMode);
        doublePGAS.startServer();
        return doublePGAS;
    }

    @Override
    protected
    PGAS< Double > newPGAS(
            final int pid,
            final String configFilePath,
            final boolean debugMode
    ) {
        final DoublePGAS doublePGAS = new DoublePGAS(pid, configFilePath);
        doublePGAS.setDebugMode(debugMode);
        doublePGAS.startServer();
        return doublePGAS;
    }

}
