package ar.edu.unrc.pellegrini.franco;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BubbleSortPGASTest {
    @Test
    void bubbleSort() {
        List< Integer > array = new ArrayList<>();
        array.addAll(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1));

        DistributedArray< Integer > distributedArray = new DistributedArray<>() {
            @Override
            public
            Integer get( long index ) {
                return array.get((int) index);
            }

            @Override
            public
            boolean imLast() {
                return true;
            }

            @Override
            public
            long lowerIndex( int pid ) {
                return 0;
            }

            @Override
            public
            void set(
                    long index,
                    Integer value
            ) {
                array.set((int) index, value);
            }

            @Override
            public
            void swap(
                    long index1,
                    long index2
            ) {
                Integer temp = array.get((int) index1);
                array.set((int) index1, array.get((int) index2));
                array.set((int) index2, temp);
            }

            @Override
            public
            long upperIndex( int pid ) {
                return array.size();
            }
        };

        BubbleSortPGAS.bubbleSort(distributedArray, 0, array.size() - 1);

        assertThat(array, is(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }

}
