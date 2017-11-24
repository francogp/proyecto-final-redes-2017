package ar.edu.unrc.pellegrini.franco.pgas;

import java.io.File;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class DistributedArrayTest {
    private static final File TEST_FILE =
            new File(DistributedArray.class.getClassLoader().getResource("ar/edu/unrc/pellegrini/franco/utils/longConfigTest.json").getFile());
    //
    //    @Test
    //    final
    //    void getSize() {
    //        PGAS< Long > longPGAS = new LongPGAS(1, TEST_FILE);
    //        assertThat(longPGAS.getSize(), is(3));
    //
    //        //noinspection ReuseOfLocalVariable
    //        longPGAS = new LongPGAS(3, TEST_FILE);
    //        assertThat(longPGAS.getSize(), is(4));
    //    }
    //
    //    @Test
    //    final
    //    void imLast() {
    //        PGAS< Long > longPGAS = new LongPGAS(1, TEST_FILE);
    //        assertThat(longPGAS.imLast(), is(false));
    //
    //        //noinspection ReuseOfLocalVariable
    //        longPGAS = new LongPGAS(3, TEST_FILE);
    //        assertThat(longPGAS.imLast(), is(true));
    //    }
    //
    //    @Test
    //    final
    //    void lowerIndex() {
    //        final PGAS< Long > longPGAS = new LongPGAS(1, TEST_FILE);
    //
    //        assertThat(longPGAS.lowerIndex(1), is(0L));
    //        assertThat(longPGAS.lowerIndex(2), is(3L));
    //        assertThat(longPGAS.lowerIndex(3), is(5L));
    //    }
    //
    //    @Test
    //    final
    //    void setAndGet() {
    //        try {
    //            final NetConfiguration< Long > netConfiguration = new NetConfiguration<>(TEST_FILE);
    //            for ( int pid = 1; pid <= netConfiguration.getProcessQuantity(); pid++ ) {
    //                final PGAS< Long > longPGAS = new LongPGAS(pid, netConfiguration);
    //                for ( long index = longPGAS.lowerIndex(); index <= longPGAS.upperIndex(); index++ ) {
    //                    longPGAS.write(index, index);
    //                    assertThat(longPGAS.read(index), is(index));
    //                }
    //            }
    //        } catch ( final Exception e ) {
    //            fail(e);
    //        }
    //    }
    //
    //    @Test
    //    final
    //    void swap() {
    //        try {
    //            for ( int pid = 1; pid <= 3; pid++ ) {
    //                final PGAS< Long > longPGAS = new LongPGAS(pid, TEST_FILE);
    //                for ( long index = longPGAS.lowerIndex(); index <= longPGAS.upperIndex(); index++ ) {
    //                    longPGAS.write(index, index);
    //                }
    //                longPGAS.swap(longPGAS.lowerIndex(), longPGAS.upperIndex());
    //                assertThat(longPGAS.read(longPGAS.lowerIndex()), is(longPGAS.upperIndex()));
    //                assertThat(longPGAS.read(longPGAS.upperIndex()), is(longPGAS.lowerIndex()));
    //            }
    //        } catch ( final Exception e ) {
    //            fail(e);
    //        }
    //    }
    //
    //    @Test
    //    final
    //    void upperIndex() {
    //        final PGAS< Long > longPGAS = new LongPGAS(1, TEST_FILE);
    //
    //        assertThat(longPGAS.upperIndex(1), is(2L));
    //        assertThat(longPGAS.upperIndex(2), is(4L));
    //        assertThat(longPGAS.upperIndex(3), is(8L));
    //    }

}
