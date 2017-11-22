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
    void getType() {
        assertThat(msg.getType(), is(READ_MSG));

        final byte[] bytes = msg.getBytes();
        final char   type  = (char) bytes[LongMessage.LONG_TYPE_BYTE_INDEX];
        assertThat(msg.getType().asChar(), is(type));

    }

    @Test
    final
    void getindexParameter() {
        assertThat(msg.getIndexParameter(), is(15L));

        final byte[] bytes          = msg.getBytes();
        final long   indexParameter = bytesToLong(bytes, LongMessage.LONG_PARAMETER_1_BYTE_INDEX, LongMessage.LONG_PARAMETER_1_BYTE_INDEX + 8);
        assertThat(msg.getIndexParameter(), is(indexParameter));
    }

    @Test
    final
    void getvalueParameter() {
        assertThat(msg.getValueParameter(), is(0L));

        final byte[] bytes          = msg.getBytes();
        final long   valueParameter = bytesToLong(bytes, LongMessage.LONG_PARAMETER_2_BYTE_INDEX, LongMessage.LONG_PARAMETER_2_BYTE_INDEX + 8);
        assertThat(msg.getValueParameter(), is(valueParameter));
    }

}
