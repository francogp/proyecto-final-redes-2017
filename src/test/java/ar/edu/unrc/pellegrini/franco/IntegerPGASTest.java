package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.distributedapi.IntegerPGAS;
import ar.edu.unrc.pellegrini.franco.distributedapi.PGAS;
import ar.edu.unrc.pellegrini.franco.utils.Configs;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class IntegerPGASTest {

    @Test
    void getRealSize() {
        final Configs         configs   = new Configs(100, 5);
        final PGAS< Integer > distArray = new IntegerPGAS(1, configs);
        assertThat(distArray.getPgasSize(), is(100L));
    }

    @Test
    void getSize() {
        Configs configs = new Configs(10, 3);

        PGAS< Integer > distArray = new IntegerPGAS(1, configs);
        assertThat(distArray.getSize(), is(3));

        configs = new Configs(10, 3);
        distArray = new IntegerPGAS(3, configs);
        assertThat(distArray.getSize(), is(4));
    }

    @Test
    void imLast() {
        Configs configs = new Configs(100, 5);

        PGAS< Integer > distArray = new IntegerPGAS(1, configs);
        assertThat(distArray.imLast(), is(false));

        configs = new Configs(100, 5);
        distArray = new IntegerPGAS(5, configs);
        assertThat(distArray.imLast(), is(true));
    }

    @Test
    void lowerIndex() {
        Configs configs = new Configs(100, 5);

        PGAS< Integer > distArray = new IntegerPGAS(1, configs);

        assertThat(distArray.lowerIndex(1), is(0L));
        assertThat(distArray.lowerIndex(2), is(20L));
        assertThat(distArray.lowerIndex(3), is(40L));
        assertThat(distArray.lowerIndex(4), is(60L));
        assertThat(distArray.lowerIndex(5), is(80L));

        configs = new Configs(10, 3);
        distArray = new IntegerPGAS(1, configs);

        assertThat(distArray.lowerIndex(1), is(0L));
        assertThat(distArray.lowerIndex(2), is(3L));
        assertThat(distArray.lowerIndex(3), is(6L));
    }

    @Test
    void setAndGet() {
        final Configs configs = new Configs(10, 3);
        for ( int pid = 1; pid <= configs.getProcessQuantity(); pid++ ) {
            final PGAS< Integer > distArray = new IntegerPGAS(pid, configs);
            for ( long i = distArray.lowerIndex(); i <= distArray.upperIndex(); i++ ) {
                distArray.write(i, (int) i);
                assertThat(distArray.read(i), is((int) i));
            }
        }
    }

    @Test
    void upperIndex() {
        Configs configs = new Configs(100, 5);

        PGAS< Integer > distArray = new IntegerPGAS(1, configs);

        assertThat(distArray.upperIndex(1), is(19L));
        assertThat(distArray.upperIndex(2), is(39L));
        assertThat(distArray.upperIndex(3), is(59L));
        assertThat(distArray.upperIndex(4), is(79L));
        assertThat(distArray.upperIndex(5), is(99L));

        configs = new Configs(10, 3);
        distArray = new IntegerPGAS(1, configs);

        assertThat(distArray.upperIndex(1), is(2L));
        assertThat(distArray.upperIndex(2), is(5L));
        assertThat(distArray.upperIndex(3), is(9L));
    }

}
