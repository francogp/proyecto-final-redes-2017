package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.net.Host;
import ar.edu.unrc.pellegrini.franco.net.NetConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ReuseOfLocalVariable", "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class NetConfigurationTest {
    @Test
    final
    void generalConfig() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File        file        = new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/utils/longConfigTest.json").getFile());

        final NetConfiguration< Long > netConfiguration = new NetConfiguration<>(file);
        assertThat(netConfiguration.getProcessQuantity(), is(3));
        assertThat(netConfiguration.size(), is(netConfiguration.getProcessQuantity()));

        Host< Long > host        = netConfiguration.getHostsConfig(1);
        InetAddress  inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch ( final UnknownHostException e ) {
            fail(e);
        }
        assertThat(host.getInetAddress(), is(inetAddress));
        Integer port = 8001;
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(9L, 1L, 2L)));

        host = netConfiguration.getHostsConfig(2);
        port = 8002;
        assertThat(host.getInetAddress(), is(inetAddress));
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(7L, 8L)));

        host = netConfiguration.getHostsConfig(3);
        port = 8003;
        assertThat(host.getInetAddress(), is(inetAddress));
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(11L, 2L, 22L, 75L)));
    }
}
