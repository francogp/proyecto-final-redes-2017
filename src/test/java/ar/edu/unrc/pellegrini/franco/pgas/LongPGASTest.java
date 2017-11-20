package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class LongPGASTest {
    private static final File FILE =
            new File(LongPGASTest.class.getClassLoader().getResource("ar/edu/unrc/pellegrini/franco/utils/configTest.json").getFile());

    @Test
    final
    void getSize() {
        final Configs< Long > configs = new Configs<>(FILE);

        PGAS< Long > longPGAS = new LongPGAS(1, configs, false);
        assertThat(longPGAS.getSize(), is(3));

        //noinspection ReuseOfLocalVariable
        longPGAS = new LongPGAS(3, configs, false);
        assertThat(longPGAS.getSize(), is(4));
    }

    @Test
    final
    void imLast() {
        final Configs< Long > configs = new Configs<>(FILE);

        PGAS< Long > longPGAS = new LongPGAS(1, configs, false);
        assertThat(longPGAS.imLast(), is(false));

        //noinspection ReuseOfLocalVariable
        longPGAS = new LongPGAS(3, configs);
        assertThat(longPGAS.imLast(), is(true));
    }

    @Test
    final
    void lowerIndex() {
        final Configs< Long > configs = new Configs<>(FILE);

        final PGAS< Long > longPGAS = new LongPGAS(1, configs, false);

        assertThat(longPGAS.lowerIndex(1), is(0L));
        assertThat(longPGAS.lowerIndex(2), is(3L));
        assertThat(longPGAS.lowerIndex(3), is(5L));
    }

    @Test
    final
    void setAndGet() {
        try {
            final Configs< Long > configs = new Configs<>(FILE);
            for ( int pid = 1; pid <= configs.getProcessQuantity(); pid++ ) {
                final PGAS< Long > longPGAS = new LongPGAS(pid, configs, false);
                for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {

                    longPGAS.write(i, i);

                    assertThat(longPGAS.read(i), is(i));
                }
            }
        } catch ( final IOException e ) {
            fail(e);
        }
    }

    @Test
    final
    void swap() {
        try {
            final Configs< Long > configs = new Configs<>(FILE);
            for ( int pid = 1; pid <= 3; pid++ ) {
                final PGAS< Long > longPGAS = new LongPGAS(pid, configs, false);
                for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {
                    longPGAS.write(i, i);
                }
                longPGAS.swap(longPGAS.lowerIndex(), longPGAS.upperIndex());
                assertThat(longPGAS.read(longPGAS.lowerIndex()), is(longPGAS.upperIndex()));
                assertThat(longPGAS.read(longPGAS.upperIndex()), is(longPGAS.lowerIndex()));
            }
        } catch ( final IOException e ) {
            fail(e);
        }
    }

    @Test
    final
    void upperIndex() {
        final Configs< Long > configs = new Configs<>(FILE);

        final PGAS< Long > longPGAS = new LongPGAS(1, configs, false);

        assertThat(longPGAS.upperIndex(1), is(2L));
        assertThat(longPGAS.upperIndex(2), is(4L));
        assertThat(longPGAS.upperIndex(3), is(8L));
    }

}
