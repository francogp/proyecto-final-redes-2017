package ar.edu.unrc.pellegrini.franco.pgas;

import ar.edu.unrc.pellegrini.franco.utils.Configs;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        PGAS< Long > longPGAS = new LongPGAS(1, FILE, false);
        assertThat(longPGAS.getSize(), is(3));

        //noinspection ReuseOfLocalVariable
        longPGAS = new LongPGAS(3, FILE, false);
        assertThat(longPGAS.getSize(), is(4));
    }

    @Test
    final
    void imLast() {
        PGAS< Long > longPGAS = new LongPGAS(1, FILE, false);
        assertThat(longPGAS.imLast(), is(false));

        //noinspection ReuseOfLocalVariable
        longPGAS = new LongPGAS(3, FILE);
        assertThat(longPGAS.imLast(), is(true));
    }

    @Test
    final
    void lowerIndex() {
        final PGAS< Long > longPGAS = new LongPGAS(1, FILE, false);

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
                final PGAS< Long > longPGAS = new LongPGAS(pid, FILE, false);
                for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {
                    longPGAS.write(i, i);
                    assertThat(longPGAS.read(i), is(i));
                }
            }
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @Test
    final
    void swap() {
        try {
            for ( int pid = 1; pid <= 3; pid++ ) {
                final PGAS< Long > longPGAS = new LongPGAS(pid, FILE, false);
                for ( long i = longPGAS.lowerIndex(); i <= longPGAS.upperIndex(); i++ ) {
                    longPGAS.write(i, i);
                }
                longPGAS.swap(longPGAS.lowerIndex(), longPGAS.upperIndex());
                assertThat(longPGAS.read(longPGAS.lowerIndex()), is(longPGAS.upperIndex()));
                assertThat(longPGAS.read(longPGAS.upperIndex()), is(longPGAS.lowerIndex()));
            }
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @Test
    final
    void upperIndex() {
        final PGAS< Long > longPGAS = new LongPGAS(1, FILE, false);

        assertThat(longPGAS.upperIndex(1), is(2L));
        assertThat(longPGAS.upperIndex(2), is(4L));
        assertThat(longPGAS.upperIndex(3), is(8L));
    }

}
