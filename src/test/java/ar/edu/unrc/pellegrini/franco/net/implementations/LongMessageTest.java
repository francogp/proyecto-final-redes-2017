package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.exceptions.InvalidValueParameterException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static ar.edu.unrc.pellegrini.franco.net.AbstractMessage.*;
import static ar.edu.unrc.pellegrini.franco.net.MessageType.READ_MSG;
import static ar.edu.unrc.pellegrini.franco.net.implementations.LongMessage.LONG_VALUE_PARAMETER_BYTE_SIZE;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToInteger;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class LongMessageTest {
    private final Message< Long > msg;

    LongMessageTest()
            throws UnknownHostException, InvalidValueParameterException {
        msg = LongMessage.getInstance();
        msg.initUsing(11, InetAddress.getLocalHost(), 0, READ_MSG, 15L, 0L);
    }

    @Test
    final
    void getIndexParameter() {
        assertThat(msg.getIndex(), is(15L));

        final byte[] bytes = msg.getAsBytes();
        final long   index = bytesToLong(bytes, INDEX_PARAMETER_BYTE_INDEX, INDEX_PARAMETER_BYTE_INDEX + INDEX_PARAMETER_BYTE_LENGTH);
        assertThat(msg.getIndex(), is(index));
    }

    @Test
    final
    void getPgasName() {
        assertThat(msg.getPgasName(), is(11));

        final byte[] bytes    = msg.getAsBytes();
        final int    pgasName = bytesToInteger(bytes, PGAS_NAME_BYTE_INDEX, PGAS_NAME_BYTE_INDEX + PGAS_NAME_BYTE_LENGTH);
        assertThat(msg.getPgasName(), is(pgasName));
    }

    @Test
    final
    void getType() {
        assertThat(msg.getType(), is(READ_MSG));

        final byte[] bytes = msg.getAsBytes();
        final char   type  = (char) bytes[TYPE_BYTE_INDEX];
        assertThat(msg.getType().asChar(), is(type));
    }

    @Test
    final
    void getValueParameter() {
        assertThat(msg.getValue(), is(0L));

        final byte[] bytes = msg.getAsBytes();
        final long   value = bytesToLong(bytes, VALUE_PARAMETER_BYTE_INDEX, VALUE_PARAMETER_BYTE_INDEX + LONG_VALUE_PARAMETER_BYTE_SIZE);
        assertThat(msg.getValue(), is(value));
    }

}
