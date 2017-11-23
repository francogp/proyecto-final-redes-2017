package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.bubblesort.implementations.DistributedBubbleSortForLong;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.ARG_DEBUG_MODE;
import static ar.edu.unrc.pellegrini.franco.bubblesort.implementations.DistributedBubbleSortForLong.ARG_CONFIG_FILE;

public final
class NetSimulationUsingLongConfigFile {
    private
    NetSimulationUsingLongConfigFile() {}

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
        final NetConfiguration< Long > configFile        = new NetConfiguration<>(arguments.parseString(ARG_CONFIG_FILE));
        Thread                         coordinatorThread = null;
        DistributedBubbleSort< Long >  coordinator       = null;
        for ( int pid = 1; pid <= configFile.size(); pid++ ) {
            final DistributedBubbleSort< Long > bubbleSortForLong =
                    new DistributedBubbleSortForLong(pid, configFile, arguments.existsFlag(ARG_DEBUG_MODE));
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
