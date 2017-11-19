package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigsTest {
    @Test
    void generalConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        File        file        = new File(classLoader.getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

        Configs configs = new Configs(file);
        assertThat(configs.getProcessQuantity(), is(3));
        assertThat(configs.getPgasSize(), is(10L));
        Map< Long, String > hosts = configs.getHosts();
        assertThat(hosts.size(), is(configs.getProcessQuantity()));
        for ( long i = 1; i <= configs.getProcessQuantity(); i++ ) {
            String address = "localhost:900" + i;
            assertThat(hosts.get(i), is(address));
        }
    }
}
