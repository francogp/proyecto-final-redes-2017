package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class LongPGASTest {
    private static File file =
            new File(LongPGASTest.class.getClassLoader().getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

    @Test
    void getSize() {
        Configs< Long > configs = new Configs<>(file);

        PGAS< Long > longPGAS = new LongPGAS(1, configs);
        assertThat(longPGAS.getSize(), is(3));

        longPGAS = new LongPGAS(3, configs);
        assertThat(longPGAS.getSize(), is(4));
    }

    @Test
    void imLast() {
        Configs< Long > configs = new Configs<>(file);

        PGAS< Long > longPGAS = new LongPGAS(1, configs);
        assertThat(longPGAS.imLast(), is(false));

        longPGAS = new LongPGAS(3, configs);
        assertThat(longPGAS.imLast(), is(true));
    }

    @Test
    void lowerIndex() {
        Configs< Long > configs = new Configs<>(file);

        PGAS< Long > longPGAS = new LongPGAS(1, configs);

        assertThat(longPGAS.lowerIndex(1), is(0L));
        assertThat(longPGAS.lowerIndex(2), is(3L));
        assertThat(longPGAS.lowerIndex(3), is(5L));
    }

    @Test
    void setAndGet() {
        final Configs< Long > configs = new Configs<>(file);
        for ( int pid = 1; pid <= configs.getProcessQuantity(); pid++ ) {
            final PGAS< Long > longPGAS = new LongPGAS(pid, configs);
            for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {
                longPGAS.write(i, i);
                assertThat(longPGAS.read(i), is(i));
            }
        }
    }

    @Test
    void swap() {
        final Configs< Long > configs = new Configs<>(file);
        for ( int pid = 1; pid <= 3; pid++ ) {
            final PGAS< Long > longPGAS = new LongPGAS(pid, configs);
            for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {
                longPGAS.write(i, i);
            }
            longPGAS.swap(longPGAS.lowerIndex(), longPGAS.upperIndex());
            assertThat(longPGAS.read(longPGAS.lowerIndex()), is(longPGAS.upperIndex()));
            assertThat(longPGAS.read(longPGAS.upperIndex()), is(longPGAS.lowerIndex()));
        }
    }

    @Test
    void upperIndex() {
        Configs< Long > configs = new Configs<>(file);

        PGAS< Long > longPGAS = new LongPGAS(1, configs);

        assertThat(longPGAS.upperIndex(1), is(2L));
        assertThat(longPGAS.upperIndex(2), is(4L));
        assertThat(longPGAS.upperIndex(3), is(8L));
    }

}
