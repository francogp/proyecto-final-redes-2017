package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings( "ClassWithoutConstructor" )
class DistributedBubbleSortTest {
    @Test
    void bubbleSort() {
        final List< Long > array = new ArrayList<>(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L));
        final PGAS< Long > PGAS  = new TestLongPGAS(array);
        DistributedBubbleSort.bubbleSort(PGAS, 0L, array.size() - 1L);
        assertThat(array, is(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static
    class TestLongPGAS
            implements PGAS< Long > {
        private final List< Long > array;

        public
        TestLongPGAS( final List< Long > array ) {this.array = array;}

        @Override
        public
        boolean andReduce( final boolean value ) {
            return false;
        }

        @Override
        public
        void barrier() {

        }

        @Override
        public
        int getPid() {
            return 1;
        }

        @Override
        public
        int getSize() {
            return array.size();
        }

        @Override
        public
        boolean imLast() {
            return true;
        }

        @Override
        public
        long lowerIndex( final int pid ) {
            return 0L;
        }

        @Override
        public
        long lowerIndex() {
            return 0L;
        }

        @Override
        public
        Long read( final long index ) {
            return array.get((int) index);
        }

        @Override
        public
        void swap(
                final long index1,
                final long index2
        ) {
            final Long temp = array.get((int) index1);
            array.set((int) index1, array.get((int) index2));
            array.set((int) index2, temp);
        }

        @Override
        public
        long upperIndex( final int pid ) {
            return array.size();
        }

        @Override
        public
        long upperIndex() {
            return array.size();
        }

        @Override
        public
        void write(
                final long index,
                final Long value
        ) {
            array.set((int) index, value);
        }
    }
}
