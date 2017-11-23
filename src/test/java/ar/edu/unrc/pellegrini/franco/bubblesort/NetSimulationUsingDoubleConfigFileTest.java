package ar.edu.unrc.pellegrini.franco.bubblesort;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( "ClassWithoutConstructor" )
class NetSimulationUsingDoubleConfigFileTest {

    private static final File TEST_FILE = new File(NetSimulationUsingDoubleConfigFileTest.class.getClassLoader()
            .getResource("ar/edu/unrc/pellegrini/franco/utils/doubleConfigTest.json")
            .getFile());

    @Test
    final
    void mainTest() {
        try {
            final String result   = '[' + NetSimulationUsingDoubleConfigFile.simulate("\"configFile=" + TEST_FILE.getPath() + '"') + ']';
            final String expected = List.of(1.1d, 2.2d, 2.7d, 7.7d, 8.8d, 9.5d, 11.1d, 22.1d, 75.9d).toString();
            assertThat(result, is(expected));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
