package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.utils.ArgumentLoader;
import ar.edu.unrc.pellegrini.franco.utils.Configs;

import java.util.stream.IntStream;

import static ar.edu.unrc.pellegrini.franco.DistributedBubbleSort.ARG_CONFIG_FILE;

public final
class NetSimulationUsingConfigFile {
    private
    NetSimulationUsingConfigFile() {}

    public static
    void main( final String... args ) {
        final ArgumentLoader arguments = new ArgumentLoader(true);
        arguments.addValidArg(ARG_CONFIG_FILE);
        arguments.loadArguments(args);
        final Configs< Long > configFile = new Configs<>(arguments.parseString(ARG_CONFIG_FILE));
        IntStream.rangeClosed(1, configFile.size())
                .mapToObj(pid -> new DistributedBubbleSort(pid, configFile))
                .map(Thread::new)
                .forEach(Thread::start);
    }
}
