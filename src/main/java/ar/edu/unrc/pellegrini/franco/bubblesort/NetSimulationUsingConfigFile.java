package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.ARG_CONFIG_FILE;
import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.ARG_DEBUG_MODE;
import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.LONG_VALUE_PARAMETER_BYTE_SIZE;

public final
class NetSimulationUsingConfigFile {
    private
    NetSimulationUsingConfigFile() {}

    public static
    void main( final String... args )
            throws InterruptedException {
        System.out.println("\nSorted final results: " + simulate(args));
    }

    public static
    String simulate( final String... args )
            throws InterruptedException {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.addValidFlag(ARG_DEBUG_MODE);

        arguments.loadArguments(args);
        final NetConfiguration< Double > configFile        = new NetConfiguration<>(arguments.parseString(ARG_CONFIG_FILE));
        Thread                           coordinatorThread = null;
        DistributedBubbleSort< Double >  coordinator       = null;
        for ( int pid = 1; pid <= configFile.size(); pid++ ) {
            final DistributedBubbleSort< Double > bubbleSortForLong = new DistributedBubbleSort(pid,
                    configFile,
                    () -> LongMessage.getInstance(), LONG_VALUE_PARAMETER_BYTE_SIZE,
                    arguments.existsFlag(ARG_DEBUG_MODE)); //TODO parametrizar y soportar double
            final Thread thread = new Thread(bubbleSortForLong);
            if ( pid == 1 ) {
                coordinatorThread = thread;
                coordinator = bubbleSortForLong;
            }
            thread.start();
        }
        coordinatorThread.join();
        return coordinator.result();
    }
}
