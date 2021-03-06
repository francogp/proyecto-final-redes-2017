package ar.edu.unrc.pellegrini.franco.pgas.implementations;

import ar.edu.unrc.pellegrini.franco.pgas.Process;
import ar.edu.unrc.pellegrini.franco.pgas.ProcessesConfigurations;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ReuseOfLocalVariable", "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class SimpleProcessesConfigurationsTest {
    @Test
    final
    void generalConfig() {
        final ClassLoader             classLoader             = getClass().getClassLoader();
        final File                    file                    =
                new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/net/processSpecificLongConfigTest.json").getFile());
        final ProcessesConfigurations processesConfigurations = SimpleProcessesConfigurations.parseFromFile(file);

        assertThat(processesConfigurations.getProcessQuantity(), is(3));
        assertThat(processesConfigurations.getProcessQuantity(), is(processesConfigurations.getProcessQuantity()));

        Process     process     = processesConfigurations.getProcessConfig(1);
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch ( final UnknownHostException e ) {
            fail(e);
        }
        assertThat(process.getInetAddress(), is(inetAddress));
        Integer port = 8001;
        assertThat(process.getPort(), is(port));
        final int pgasName = 99;
        assertThat(process.getValues(pgasName).isEmpty(), is(true));

        process = processesConfigurations.getProcessConfig(2);
        port = 8002;
        assertThat(process.getInetAddress(), is(inetAddress));
        assertThat(process.getPort(), is(port));
        assertThat(process.getValues(pgasName), is(Arrays.asList(7L, 8L)));

        process = processesConfigurations.getProcessConfig(3);
        port = 8003;
        assertThat(process.getInetAddress(), is(inetAddress));
        assertThat(process.getPort(), is(port));
        assertThat(process.getValues(pgasName).isEmpty(), is(true));
    }
}
