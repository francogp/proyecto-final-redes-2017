package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings( { "ReuseOfLocalVariable", "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class BytesConversionTest {
    @Test
    final
    void doubleGeneralTest() {
        Double       expected   = Math.PI;
        byte[]       bytes      = BytesConversion.doubleToBytes(expected);
        final byte[] largeBytes = new byte[16];
        final int    desPos     = 5;
        System.arraycopy(bytes, 0, largeBytes, desPos, 8);
        Double output = BytesConversion.bytesToDouble(bytes);
        assertThat(output, is(expected));
        output = BytesConversion.bytesToDouble(largeBytes, 5, desPos + 8);
        assertThat(output, is(expected));

        expected = Double.MAX_VALUE;
        bytes = BytesConversion.doubleToBytes(expected);
        output = BytesConversion.bytesToDouble(bytes);
        assertThat(output, is(expected));

        expected = Double.MIN_VALUE;
        bytes = BytesConversion.doubleToBytes(expected);
        output = BytesConversion.bytesToDouble(bytes);
        assertThat(output, is(expected));
    }

    @Test
    final
    void integerGeneralTest() {
        int          expected   = 95486100;
        byte[]       bytes      = BytesConversion.integerToBytes(expected);
        final byte[] largeBytes = new byte[16];
        final int    desPos     = 5;
        System.arraycopy(bytes, 0, largeBytes, desPos, 4);
        int output = BytesConversion.bytesToInteger(bytes);
        assertThat(output, is(expected));
        output = BytesConversion.bytesToInteger(largeBytes, 5, desPos + 4);
        assertThat(output, is(expected));

        expected = Integer.MAX_VALUE;
        bytes = BytesConversion.integerToBytes(expected);
        output = BytesConversion.bytesToInteger(bytes);
        assertThat(output, is(expected));

        expected = Integer.MIN_VALUE;
        bytes = BytesConversion.integerToBytes(expected);
        output = BytesConversion.bytesToInteger(bytes);
        assertThat(output, is(expected));
    }

    @Test
    final
    void longGeneralTest() {
        Long         expected   = 95486100L;
        byte[]       bytes      = BytesConversion.longToBytes(expected);
        final byte[] largeBytes = new byte[16];
        final int    desPos     = 5;
        System.arraycopy(bytes, 0, largeBytes, desPos, 8);
        Long output = BytesConversion.bytesToLong(bytes);
        assertThat(output, is(expected));
        output = BytesConversion.bytesToLong(largeBytes, 5, desPos + 8);
        assertThat(output, is(expected));

        expected = Long.MAX_VALUE;
        bytes = BytesConversion.longToBytes(expected);
        output = BytesConversion.bytesToLong(bytes);
        assertThat(output, is(expected));

        expected = Long.MIN_VALUE;
        bytes = BytesConversion.longToBytes(expected);
        output = BytesConversion.bytesToLong(bytes);
        assertThat(output, is(expected));
    }
}
