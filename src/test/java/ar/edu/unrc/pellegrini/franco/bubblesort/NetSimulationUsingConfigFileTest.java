package ar.edu.unrc.pellegrini.franco.bubblesort;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( "ClassWithoutConstructor" )
class NetSimulationUsingConfigFileTest {

    @Test
    final
    void doubleNetSimulationTest() {
        try {
            final File testFile = new File(NetSimulationUsingConfigFileTest.class.getClassLoader()
                    .getResource("ar/edu/unrc/pellegrini/franco/bubblesort/doubleConfigTest.json")
                    .getFile());
            final String result   = '[' + NetSimulationUsingConfigFile.simulate("\"configFile=" + testFile.getPath() + '"') + ']';
            final String expected = Arrays.asList(1.1d, 2.2d, 2.7d, 7.7d, 8.8d, 9.5d, 11.1d, 22.1d, 75.9d).toString();
            assertThat(result, is(expected));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @Test
    final
    void longNetSimulationTest() {
        try {
            final File testFile = new File(NetSimulationUsingConfigFileTest.class.getClassLoader()
                    .getResource("ar/edu/unrc/pellegrini/franco/bubblesort/longConfigTest.json")
                    .getFile());
            final String result   = '[' + NetSimulationUsingConfigFile.simulate("\"configFile=" + testFile.getPath() + '"') + ']';
            final String expected = Arrays.asList(1L, 2L, 2L, 7L, 8L, 9L, 11L, 22L, 75L).toString();
            assertThat(result, is(expected));
        } catch ( final Exception e ) {
            fail(e);
        }
    }
}
