package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.bubblesort.implementations.DistributedBubbleSortForDouble;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import static ar.edu.unrc.pellegrini.franco.bubblesort.implementations.DistributedBubbleSortForLong.ARG_CONFIG_FILE;

public final
class NetSimulationUsingDoubleConfigFile {
    private
    NetSimulationUsingDoubleConfigFile() {}

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
        arguments.loadArguments(args);
        final NetConfiguration< Double > configFile        = new NetConfiguration<>(arguments.parseString(ARG_CONFIG_FILE));
        Thread                           coordinatorThread = null;
        DistributedBubbleSort< Double >  coordinator       = null;
        for ( int pid = 1; pid <= configFile.size(); pid++ ) {
            final DistributedBubbleSort< Double > bubbleSortForLong = new DistributedBubbleSortForDouble(pid, arguments.parseString(ARG_CONFIG_FILE));
            final Thread                          thread            = new Thread(bubbleSortForLong);
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
