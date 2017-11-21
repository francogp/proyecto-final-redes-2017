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
            final List< Long > array      = new ArrayList<>(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L));
            long               lowerIndex = 0L;
            long               upperIndex = array.size() - 1L;
            final PGAS< Long > PGAS       = new TestLongPGAS(array, lowerIndex, upperIndex);
            DistributedBubbleSort.bubbleSort(PGAS, lowerIndex, upperIndex);
            assertThat(array, is(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));

            final List< Long > array2      = new ArrayList<>(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L));
            long               lowerIndex2 = 3L;
            long               upperIndex2 = array.size() - 4L;
            final PGAS< Long > PGAS2       = new TestLongPGAS(array2, lowerIndex2, upperIndex2);
            DistributedBubbleSort.bubbleSort(PGAS2, lowerIndex2, upperIndex2);
            assertThat(array2, is(List.of(9L, 8L, 7L, 4L, 5L, 6L, 3L, 2L, 1L)));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @SuppressWarnings( "ClassWithoutNoArgConstructor" )
    private static
    class TestLongPGAS
            implements PGAS< Long > {
        private final List< Long > array;
        private       long         lowerIndex;
        private       long         upperIndex;

        public
        TestLongPGAS(
                final List< Long > array,
                final long lowerIndex,
                final long upperIndex
        ) {
            this.array = array;
            this.lowerIndex = lowerIndex;
            this.upperIndex = upperIndex;
        }

        @Override
        public final
        boolean andReduce( final boolean value ) {
            return false;
        }

        @Override
        public
        String asString() {
            return array.toString();
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
            throw new IllegalStateException();
        }

        @Override
        public final
        long lowerIndex() {
            return lowerIndex;
        }

        @Override
        public final
        Long read( final long index ) {
            if ( index < lowerIndex || index > upperIndex ) {
                throw new IllegalArgumentException("index " + index + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            return array.get((int) index);
        }

        @Override
        public final
        void swap(
                final long index1,
                final long index2
        ) {
            if ( index1 < lowerIndex || index1 > upperIndex ) {
                throw new IllegalArgumentException("index " + index1 + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            if ( index2 < lowerIndex || index2 > upperIndex ) {
                throw new IllegalArgumentException("index " + index2 + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            final Long temp = array.get((int) index1);
            array.set((int) index1, array.get((int) index2));
            array.set((int) index2, temp);
        }

        @Override
        public final
        long upperIndex( final int pid ) {
            throw new IllegalStateException();
        }

        @Override
        public final
        long upperIndex() {
            return upperIndex;
        }

        @Override
        public final
        void write(
                final long index,
                final Long value
        ) {
            if ( index < lowerIndex || index > upperIndex ) {
                throw new IllegalArgumentException("index " + index + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            array.set((int) index, value);
        }
    }
}
