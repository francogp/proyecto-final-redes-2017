package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.pgas.implementations.SimpleProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.File;

import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.*;

/**
 * Runs several processes as threads to solve BubbleSort as a distributed algorithm, for testing purposes.
 */
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

        final File                    configFile              = new File(arguments.parseString(ARG_CONFIG_FILE));
        final ProcessesConfigurations processesConfigurations = SimpleProcessesConfigurations.parseFromFile(configFile);
        Thread                        coordinatorThread       = null;
        DistributedBubbleSort< ? >    coordinator             = null;
        final int                     processQuantity         = processesConfigurations.getProcessQuantity();
        final boolean                 debugMode               = arguments.existsFlag(ARG_DEBUG_MODE);
        for ( int pid = 1; pid <= processQuantity; pid++ ) {
            final Runnable bubbleSort = getRunnableBubbleSort(pid, processesConfigurations, debugMode);
            final Thread   thread     = new Thread(bubbleSort);
            if ( pid == 1 ) {
                coordinatorThread = thread;
                coordinator = (DistributedBubbleSort< ? >) bubbleSort;
            }
            thread.start();
        }
        coordinatorThread.join();
        return coordinator.result();
    }
}
