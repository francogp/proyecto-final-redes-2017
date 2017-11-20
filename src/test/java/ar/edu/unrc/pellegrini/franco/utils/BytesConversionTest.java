package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BytesConversionTest {
    @Test
    void generalTest() {
        Long   expected = 95486100L;
        byte[] bytes    = BytesConversion.longToBytes(expected);
        Long   output   = BytesConversion.bytesToLong(bytes);
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
