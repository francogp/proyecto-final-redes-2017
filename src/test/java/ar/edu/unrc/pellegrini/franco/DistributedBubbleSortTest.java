package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( "ClassWithoutConstructor" )
class DistributedBubbleSortTest {
    @Test
    final
    void bubbleSort() {
        try {
            final List< Long > array = new ArrayList<>(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L));
            final PGAS< Long > PGAS  = new TestLongPGAS(array);
            DistributedBubbleSort.bubbleSort(PGAS, 0L, array.size() - 1L);
            assertThat(array, is(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static
    class TestLongPGAS
            implements PGAS< Long > {
        private final List< Long > array;

        public
        TestLongPGAS( final List< Long > array ) {this.array = array;}

        @Override
        public final
        boolean andReduce( final boolean value ) {
            return false;
        }

        @Override
        public
        void barrier() {

        }

        @Override
        public final
        int getPid() {
            return 1;
        }

        @Override
        public final
        int getSize() {
            return array.size();
        }

        @Override
        public final
        boolean imLast() {
            return true;
        }

        @Override
        public
        boolean isCoordinator() {
            return false;
        }

        @Override
        public final
        long lowerIndex( final int pid ) {
            return 0L;
        }

        @Override
        public final
        long lowerIndex() {
            return 0L;
        }

        @Override
        public final
        Long read( final long index ) {
            return array.get((int) index);
        }

        @Override
        public final
        void swap(
                final long index1,
                final long index2
        ) {
            final Long temp = array.get((int) index1);
            array.set((int) index1, array.get((int) index2));
            array.set((int) index2, temp);
        }

        @Override
        public final
        long upperIndex( final int pid ) {
            return array.size();
        }

        @Override
        public final
        long upperIndex() {
            return array.size();
        }

        @Override
        public final
        void write(
                final long index,
                final Long value
        ) {
            array.set((int) index, value);
        }
    }
}
