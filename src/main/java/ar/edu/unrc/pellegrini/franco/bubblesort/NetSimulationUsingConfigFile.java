package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage;
import ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage;
import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.pgas.SimpleProcessesConfigurations;
import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;

import java.io.File;

import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.ARG_CONFIG_FILE;
import static ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort.ARG_DEBUG_MODE;
import static ar.edu.unrc.pellegrini.franco.net.implementations.DoubleMessage.DOUBLE_VALUE_PARAMETER_BYTE_SIZE;
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

        final File                         configFile              = new File(arguments.parseString(ARG_CONFIG_FILE));
        final ProcessesConfigurations< ? > processesConfigurations = SimpleProcessesConfigurations.parseFromFile(configFile);
        Thread                             coordinatorThread       = null;
        DistributedBubbleSort< ? >         coordinator             = null;
        final int                          processQuantity         = processesConfigurations.getProcessQuantity();
        for ( int pid = 1; pid <= processQuantity; pid++ ) {
            final Runnable bubbleSort;
            switch ( processesConfigurations.getPgasDataType() ) {
                case "Long":
                    bubbleSort = new DistributedBubbleSort<>(pid,
                            (ProcessesConfigurations< Long >) processesConfigurations,
                            LongMessage::getInstance,
                            LONG_VALUE_PARAMETER_BYTE_SIZE,
                            arguments.existsFlag(ARG_DEBUG_MODE));
                    break;
                case "Double":
                    bubbleSort = new DistributedBubbleSort<>(pid,
                            (ProcessesConfigurations< Double >) processesConfigurations,
                            DoubleMessage::getInstance,
                            DOUBLE_VALUE_PARAMETER_BYTE_SIZE,
                            arguments.existsFlag(ARG_DEBUG_MODE));
                    break;
                default:
                    throw new IllegalArgumentException("unknown dataType implementation");
            }
            final Thread thread = new Thread(bubbleSort);
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
