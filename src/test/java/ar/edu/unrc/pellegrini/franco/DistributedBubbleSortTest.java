package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.pgas.PGAS;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class DistributedBubbleSortTest {
    @Test
    void bubbleSort() {
        final List< Long > array = new ArrayList<>();
        array.addAll(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L));

        final PGAS< Long > PGAS = new PGAS<>() {
            @Override
            public
            boolean andReduce( boolean value ) {
                return false;
            }

            @Override
            public
            void barrier() {

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
                return 0;
            }

            @Override
            public
            long lowerIndex() {
                return 0;
            }

            @Override
            public
            Long read( final long index ) {
                return array.get((int) index);
            }

            @Override
            public
            void swap(
                    long index1,
                    long index2
            ) {
                Long temp = array.get((int) index1);
                array.set((int) index1, array.get((int) index2));
                array.set((int) index2, temp);
            }

            @Override
            public
            long upperIndex( int pid ) {
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
        };

        DistributedBubbleSort.bubbleSort(PGAS, 0, array.size() - 1);

        assertThat(array, is(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
    }

}
