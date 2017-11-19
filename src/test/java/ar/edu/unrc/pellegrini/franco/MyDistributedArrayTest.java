package ar.edu.unrc.pellegrini.franco;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MyDistributedArrayTest {

    @Test
    void getRealSize() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 100);
        assertThat(distArray.getRealSize(), is(100L));
    }

    @Test
    void getSize() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 3);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 10);
        assertThat(distArray.getSize(), is(3));

        middleware = new MyMiddleware<>(3, 3);
        distArray = new MyDistributedArray<>(Integer.class, middleware, 10);
        assertThat(distArray.getSize(), is(4));
    }

    @Test
    void imLast() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 100);
        assertThat(distArray.imLast(), is(false));

        middleware = new MyMiddleware<>(5, 5);
        distArray = new MyDistributedArray<>(Integer.class, middleware, 100);
        assertThat(distArray.imLast(), is(true));
    }

    @Test
    void lowerIndex() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 100);

        assertThat(distArray.lowerIndex(1), is(0L));
        assertThat(distArray.lowerIndex(2), is(20L));
        assertThat(distArray.lowerIndex(3), is(40L));
        assertThat(distArray.lowerIndex(4), is(60L));
        assertThat(distArray.lowerIndex(5), is(80L));

        middleware = new MyMiddleware<>(1, 3);
        distArray = new MyDistributedArray<>(Integer.class, middleware, 10);

        assertThat(distArray.lowerIndex(1), is(0L));
        assertThat(distArray.lowerIndex(2), is(3L));
        assertThat(distArray.lowerIndex(3), is(6L));
    }

    @Test
    void setAndGet() {
        for ( int pid = 1; pid <= 3; pid++ ) {
            Middleware< Integer >       middleware = new MyMiddleware<>(pid, 3);
            DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 10);
            for ( long i = distArray.lowerIndex(); i <= distArray.upperIndex(); i++ ) {
                distArray.set(i, (int) i);
                assertThat(distArray.get(i), is((int) i));
            }
        }
    }

    @Test
    void swap() {
        for ( int pid = 1; pid <= 3; pid++ ) {
            Middleware< Integer >       middleware = new MyMiddleware<>(pid, 3);
            DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 10);
            for ( long i = distArray.lowerIndex(); i <= distArray.upperIndex(); i++ ) {
                distArray.set(i, (int) i);
            }
            distArray.swap(distArray.lowerIndex(), distArray.upperIndex());
            assertThat(distArray.get(distArray.lowerIndex()), is((int) distArray.upperIndex()));
            assertThat(distArray.get(distArray.upperIndex()), is((int) distArray.lowerIndex()));
        }
    }

    @Test
    void upperIndex() {
        Middleware< Integer >       middleware = new MyMiddleware<>(1, 5);
        DistributedArray< Integer > distArray  = new MyDistributedArray<>(Integer.class, middleware, 100);

        assertThat(distArray.upperIndex(1), is(19L));
        assertThat(distArray.upperIndex(2), is(39L));
        assertThat(distArray.upperIndex(3), is(59L));
        assertThat(distArray.upperIndex(4), is(79L));
        assertThat(distArray.upperIndex(5), is(99L));

        middleware = new MyMiddleware<>(1, 3);
        distArray = new MyDistributedArray<>(Integer.class, middleware, 10);

        assertThat(distArray.upperIndex(1), is(2L));
        assertThat(distArray.upperIndex(2), is(5L));
        assertThat(distArray.upperIndex(3), is(9L));
    }

}
