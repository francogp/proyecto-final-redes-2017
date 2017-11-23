package ar.edu.unrc.pellegrini.franco.bubblesort.implementations;

import ar.edu.unrc.pellegrini.franco.bubblesort.AbstractDistributedBubbleSort;
import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.LongPGAS;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

@SuppressWarnings( "ClassWithoutNoArgConstructor" )
public final
class DistributedBubbleSortForLong
        extends AbstractDistributedBubbleSort< Long > {

    public
    DistributedBubbleSortForLong(
            final int pid,
            final String configFilePath,
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
        final int      pid        = arguments.parseInteger(ARG_PID);
        final Runnable bubbleSort =
                new DistributedBubbleSortForLong(pid, arguments.parseString(ARG_CONFIG_FILE), arguments.existsFlag(ARG_DEBUG_MODE));
        bubbleSort.run();
    }

    @Override
    protected
    PGAS< Long > newPGAS(
            final int pid,
            final String configFilePath,
            final boolean debugMode
    ) {
        final LongPGAS longPGAS = new LongPGAS(pid, configFilePath);
        longPGAS.setDebugMode(debugMode);
        return longPGAS;
    }

}
