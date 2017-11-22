package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import org.junit.jupiter.api.Test;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.READ_MSG;
import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings( "ClassWithoutConstructor" )
class LongMessageTest {
    private final Message< Long > msg = new LongMessage(null, 0, READ_MSG, 15L, 0L);

    @Test
    final
    void getParameter1() {
        assertThat(msg.getParameter1(), is(15L));

        final byte[] bytes      = msg.getBytes();
        final long   parameter1 = bytesToLong(bytes, LongMessage.PARAMETER_1_BYTE_INDEX, LongMessage.PARAMETER_1_BYTE_INDEX + 8);
        assertThat(msg.getParameter1(), is(parameter1));
    }

    @Test
    final
    void getParameter2() {
        assertThat(msg.getParameter2(), is(0L));

        final byte[] bytes      = msg.getBytes();
        final long   parameter2 = bytesToLong(bytes, LongMessage.PARAMETER_2_BYTE_INDEX, LongMessage.PARAMETER_2_BYTE_INDEX + 8);
        assertThat(msg.getParameter2(), is(parameter2));
    }

    @Test
    final
    void getType() {
        assertThat(msg.getType(), is(READ_MSG));

        final byte[] bytes = msg.getBytes();
        final char   type  = (char) bytes[LongMessage.TYPE_BYTE_INDEX];
        assertThat(msg.getType().asChar(), is(type));

    }

}
