package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.NetConfiguration;

import static ar.edu.unrc.pellegrini.franco.DistributedBubbleSort.ARG_CONFIG_FILE;

public final
class NetSimulationUsingConfigFile {
    private
    NetSimulationUsingConfigFile() {}

    public static
    void main( final String... args )
            throws InterruptedException {
        System.out.println(run(args));
    }

    public static
    String run( final String... args )
            throws InterruptedException {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.loadArguments(args);
        final NetConfiguration< Long > configFile        = new NetConfiguration<>(arguments.parseString(ARG_CONFIG_FILE));
        Thread                         coordinatorThread = null;
        DistributedBubbleSort          coordinator       = null;
        for ( int pid = 1; pid <= configFile.size(); pid++ ) {
            final DistributedBubbleSort distributedBubbleSort = new DistributedBubbleSort(pid, arguments.parseString(ARG_CONFIG_FILE));
            Thread                      thread                = new Thread(distributedBubbleSort);
            if ( pid == 1 ) {
                coordinatorThread = thread;
                coordinator = distributedBubbleSort;
            }
            thread.start();
        }
        coordinatorThread.join();
        return coordinator.result();
    }
}
