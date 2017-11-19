package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigsTest {
    @Test
    void generalConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        File        file        = new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

        Configs< Long > configs = new Configs<>(file);
        assertThat(configs.getProcessQuantity(), is(3));
        assertThat(configs.size(), is(configs.getProcessQuantity()));

        Configs.HostConfig< Long > host    = configs.getHostsConfig(1);
        String                     address = "localhost:9001";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(9L, 1L, 2L)));

        host = configs.getHostsConfig(2);
        address = "localhost:9002";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(7L, 8L)));

        host = configs.getHostsConfig(3);
        address = "localhost:9003";
        assertThat(host.getLocation(), is(address));
        assertThat(host.getToSort(), is(List.of(11L, 2L, 22L, 75L)));
    }
}
