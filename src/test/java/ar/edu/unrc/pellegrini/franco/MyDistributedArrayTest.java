package ar.edu.unrc.pellegrini.franco;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MyDistributedArrayTest {

    @Test
    void lowerIndex() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > dArray     = new MyDistributedArray<>(middleware, 100);

        assertThat(dArray.lowerIndex(1), is(0L));
        assertThat(dArray.lowerIndex(2), is(20L));
        assertThat(dArray.lowerIndex(3), is(40L));
        assertThat(dArray.lowerIndex(4), is(60L));
        assertThat(dArray.lowerIndex(5), is(80L));
    }

    @Test
    void upperIndex() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > dArray     = new MyDistributedArray<>(middleware, 100);

        assertThat(dArray.upperIndex(1), is(19L));
        assertThat(dArray.upperIndex(2), is(39L));
        assertThat(dArray.upperIndex(3), is(59L));
        assertThat(dArray.upperIndex(4), is(79L));
        assertThat(dArray.upperIndex(5), is(99L));
    }

}
