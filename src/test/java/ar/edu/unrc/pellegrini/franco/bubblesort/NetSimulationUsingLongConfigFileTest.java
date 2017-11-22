package ar.edu.unrc.pellegrini.franco.bubblesort;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class NetSimulationUsingLongConfigFileTest {
    @Test
    void mainTest() {
        try {
            final String result = "[" + NetSimulationUsingLongConfigFile.simulate("configFile=exampleConfig.json") + "]";
            final String expected = List.of(-784L,
                    -65L,
                    -54L,
                    -33L,
                    -8L,
                    -7L,
                    -5L,
                    -3L,
                    -1L,
                    -1L,
                    1L,
                    1L,
                    1L,
                    1L,
                    2L,
                    2L,
                    2L,
                    2L,
                    2L,
                    3L,
                    5L,
                    5L,
                    7L,
                    8L,
                    9L,
                    11L,
                    20L,
                    21L,
                    22L,
                    75L,
                    85L,
                    88L,
                    98L,
                    99L,
                    101L,
                    902L,
                    999L).toString();
            assertThat(result, is(expected));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
