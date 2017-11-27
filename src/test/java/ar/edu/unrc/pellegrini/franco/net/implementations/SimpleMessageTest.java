package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.exceptions.InvalidValueParameterException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.READ_MSG;
import static ar.edu.unrc.pellegrini.franco.net.implementations.SimpleMessage.*;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SimpleMessageTest {
    private static final long value = 1995724L;
    private final Message msg;

    SimpleMessageTest()
            throws UnknownHostException, InvalidValueParameterException {
        msg = new SimpleMessage();
        msg.initUsing(11, InetAddress.getLocalHost(), 0, READ_MSG, 15L, 8, longToBytes(value));
    }

    @Test
    final
    void getIndexParameter() {
        assertThat(msg.getIndex(), is(15L));

        final byte[] bytes = msg.getMessageAsBytes();
        final long   index = bytesToLong(bytes, INDEX_BYTE_INDEX, INDEX_BYTE_INDEX + INDEX_BYTE_LENGTH);
        assertThat(msg.getIndex(), is(index));
    }

    @Test
    final
    void getPgasName() {
        assertThat(msg.getPgasName(), is(11));

        final byte[] bytes    = msg.getMessageAsBytes();
        final int    pgasName = bytesToInteger(bytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH);
        assertThat(msg.getPgasName(), is(pgasName));
    }

    @Test
    final
    void getType() {
        assertThat(msg.getType(), is(READ_MSG));

        final byte[] bytes = msg.getMessageAsBytes();
        final char   type  = (char) bytes[TYPE_BYTE_INDEX];
        assertThat(msg.getType().asChar(), is(type));
    }

    @Test
    final
    void getValueAsBytes() {
        assertThat(msg.getValueAsBytes(), is(longToBytes(value)));

        final byte[] bytes       = msg.getMessageAsBytes();
        final long   parsedValue = bytesToLong(bytes, VALUE_DATA_BYTE_INDEX, VALUE_DATA_BYTE_INDEX + msg.getValueBytesSize());
        assertThat(parsedValue, is(value));
    }

}
