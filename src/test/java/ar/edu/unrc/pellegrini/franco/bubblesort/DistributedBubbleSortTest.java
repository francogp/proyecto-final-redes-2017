package ar.edu.unrc.pellegrini.franco.bubblesort;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.longToBytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( "ClassWithoutConstructor" )
class DistributedBubbleSortTest {
    @Test
    final
    void bubbleSort() {
        try {
            final List< Long > array      = Arrays.asList(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L);
            final long         lowerIndex = 0L;
            final long         upperIndex = array.size() - 1L;
            final PGAS< Long > PGAS       = new TestLongPGAS(array, lowerIndex, upperIndex);
            DistributedBubbleSort.bubbleSort(PGAS, lowerIndex, upperIndex);
            assertThat(array, is(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));

            final List< Long > array2      = Arrays.asList(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L);
            final long         lowerIndex2 = 3L;
            final long         upperIndex2 = array.size() - 4L;
            final PGAS< Long > PGAS2       = new TestLongPGAS(array2, lowerIndex2, upperIndex2);
            DistributedBubbleSort.bubbleSort(PGAS2, lowerIndex2, upperIndex2);
            assertThat(array2, is(Arrays.asList(9L, 8L, 7L, 4L, 5L, 6L, 3L, 2L, 1L)));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

    @SuppressWarnings( { "ClassWithoutNoArgConstructor", "AssignmentToCollectionOrArrayFieldFromParameter" } )
    private static
    class TestLongPGAS
            implements PGAS< Long > {
        private final List< Long > array;
        private final long         lowerIndex;
        private final long         upperIndex;

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
        String asString() {
            return array.toString();
        }

        @Override
        public
        int getDataTypeSize() {
            return 8;
        }

        @Override
        public
        int getName() {
            return 0;
        }

        @Override
        public final
        int getSize() {
            return array.size();
        }

        @Override
        public final
        long lowerIndex( final int pid ) {
            throw new IllegalStateException("Not used");
        }

        @Override
        public final
        long lowerIndex() {
            return lowerIndex;
        }

        @Override
        public
        Long parseBytesToData(
                byte[] valueAsBytes,
                int valueBytesSize
        ) {
            return bytesToLong(valueAsBytes, 0, valueBytesSize);
        }

        @Override
        public final
        Long read( final long index ) {
            if ( ( index < lowerIndex ) || ( index > upperIndex ) ) {
                throw new IllegalArgumentException("index " + index + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            return array.get(Long.valueOf(index).intValue());
        }

        @Override
        public
        byte[] readAsBytes( long index )
                throws Exception {
            return longToBytes(array.get((int) index));
        }

        @Override
        public
        void setDebugMode( final boolean enable ) {
        }

        @Override
        public final
        void swap(
                final long index1,
                final long index2
        ) {
            if ( ( index1 < lowerIndex ) || ( index1 > upperIndex ) ) {
                throw new IllegalArgumentException("index " + index1 + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            if ( ( index2 < lowerIndex ) || ( index2 > upperIndex ) ) {
                throw new IllegalArgumentException("index " + index2 + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            final Long temp = array.get((int) index1);
            array.set((int) index1, array.get((int) index2));
            array.set((int) index2, temp);
        }

        @Override
        public final
        long upperIndex( final int pid ) {
            throw new IllegalStateException("Not used");
        }

        @Override
        public final
        long upperIndex() {
            return upperIndex;
        }

        @Override
        public
        byte[] valueToBytesArray( Long value ) {
            return longToBytes(value);
        }

        @Override
        public final
        void write(
                final long index,
                final Long value
        ) {
            if ( ( index < lowerIndex ) || ( index > upperIndex ) ) {
                throw new IllegalArgumentException("index " + index + " is not index<" + lowerIndex + " || index>" + upperIndex);
            }
            array.set(Long.valueOf(index).intValue(), value);
        }

        @Override
        public
        void writeAsBytes(
                long index,
                byte[] valueAsByte
        )
                throws Exception {
            array.set((int) index, bytesToLong(valueAsByte, 0, valueAsByte.length));
        }
    }
}
