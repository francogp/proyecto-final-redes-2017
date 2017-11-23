package ar.edu.unrc.pellegrini.franco.bubblesort;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( "ClassWithoutConstructor" )
class NetSimulationUsingLongConfigFileTest {

    private static final File TEST_FILE = new File(NetSimulationUsingLongConfigFileTest.class.getClassLoader()
            .getResource("ar/edu/unrc/pellegrini/franco/utils/longConfigTest.json")
            .getFile());

    @Test
    final
    void mainTest() {
        try {
            final String result   = '[' + NetSimulationUsingLongConfigFile.simulate("\"configFile=" + TEST_FILE.getPath() + '"') + ']';
            final String expected = List.of(1L, 2L, 2L, 7L, 8L, 9L, 11L, 22L, 75L).toString();
            assertThat(result, is(expected));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
