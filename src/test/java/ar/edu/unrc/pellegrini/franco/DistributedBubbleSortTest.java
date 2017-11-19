package ar.edu.unrc.pellegrini.franco;

import ar.edu.unrc.pellegrini.franco.distributedapi.DistributedArray;
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

        final DistributedArray< Integer > DistributedArray = new DistributedArray<>() {
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
            Integer get( final long index ) {
                return array.get((int) index);
            }

            @Override
            public
            long getDistributedSize() {
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
            void set(
                    final long index,
                    final Integer value
            ) {
                array.set((int) index, value);
            }

            @Override
            public
            void swap(
                    final long index1,
                    final long index2
            ) {
                final Integer temp = array.get((int) index1);
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
        };

        DistributedBubbleSort.bubbleSort(DistributedArray, 0, array.size() - 1);

        assertThat(array, is(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }

}
