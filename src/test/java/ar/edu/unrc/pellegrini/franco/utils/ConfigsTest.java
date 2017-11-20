package ar.edu.unrc.pellegrini.franco.utils;

import ar.edu.unrc.pellegrini.franco.utils.Configs.HostConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings( { "ReuseOfLocalVariable", "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class ConfigsTest {
    @Test
    void generalConfig() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File        file        = new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

        final Configs< Long > configs = new Configs<>(file);
        assertThat(configs.getProcessQuantity(), is(3));
        assertThat(configs.size(), is(configs.getProcessQuantity()));

        HostConfig< Long > host    = configs.getHostsConfig(1L);
        String             address = "localhost:9001";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(9L, 1L, 2L)));

        host = configs.getHostsConfig(2L);
        address = "localhost:9002";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(7L, 8L)));

        host = configs.getHostsConfig(3L);
        address = "localhost:9003";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(11L, 2L, 22L, 75L)));
    }
}
