package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ReuseOfLocalVariable", "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class ConfigsTest {
    @Test
    final
    void generalConfig() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File        file        = new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

        final Configs< Long > configs = new Configs<>(file);
        assertThat(configs.getProcessQuantity(), is(3));
        assertThat(configs.size(), is(configs.getProcessQuantity()));

        HostConfig< Long > host        = configs.getHostsConfig(1);
        InetAddress        inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch ( final UnknownHostException e ) {
            fail(e);
        }
        assertThat(host.getInetAddress(), is(inetAddress));
        Integer port = 9001;
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(9L, 1L, 2L)));

        host = configs.getHostsConfig(2);
        port = 9002;
        assertThat(host.getInetAddress(), is(inetAddress));
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(7L, 8L)));

        host = configs.getHostsConfig(3);
        port = 9003;
        assertThat(host.getInetAddress(), is(inetAddress));
        assertThat(host.getPort(), is(port));
        assertThat(host.getToSort(), is(List.of(11L, 2L, 22L, 75L)));
    }
}
