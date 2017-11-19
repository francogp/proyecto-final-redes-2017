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
        final List< Integer > array = new ArrayList<>();
        array.addAll(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1));

        final PGAS< Integer > PGAS = new PGAS<>() {
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
            long getPgasSize() {
                return array.size();
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
            Integer read( final long index ) {
                return array.get((int) index);
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
                    final Integer value
            ) {
                array.set((int) index, value);
            }
        };

        DistributedBubbleSort.bubbleSort(PGAS, 0, array.size() - 1);

        assertThat(array, is(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }

}
