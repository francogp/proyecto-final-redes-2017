package ar.edu.unrc.pellegrini.franco.pgas.net;

import org.junit.jupiter.api.Test;

import static ar.edu.unrc.pellegrini.franco.utils.BytesConversion.bytesToLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MessageTest {
    private Message msg = new Message(null, 0, Message.READ_MSG, 15L);

    @Test
    void getParameter1() {
        assertThat(msg.getParameter1(), is(15L));

        byte[] bytes      = msg.getBytes();
        long   parameter1 = bytesToLong(bytes, Message.PARAMETER_1_BYTE_INDEX, Message.PARAMETER_1_BYTE_INDEX + 8);
        assertThat(msg.getParameter1(), is(parameter1));
    }

    @Test
    void getParameter2() {
        assertThat(msg.getParameter2(), is(0L));

        byte[] bytes      = msg.getBytes();
        long   parameter2 = bytesToLong(bytes, Message.PARAMETER_2_BYTE_INDEX, Message.PARAMETER_2_BYTE_INDEX + 8);
        assertThat(msg.getParameter2(), is(parameter2));
    }

    @Test
    void getType() {
        assertThat(msg.getType(), is(Message.READ_MSG));

        byte[] bytes = msg.getBytes();
        char   type  = (char) bytes[Message.TYPE_BYTE_INDEX];
        assertThat(msg.getType(), is(type));

    }

}
