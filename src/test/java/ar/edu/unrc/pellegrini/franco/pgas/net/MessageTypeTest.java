package ar.edu.unrc.pellegrini.franco.pgas.net;

import org.junit.jupiter.api.Test;

import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.AND_REDUCE_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MessageTypeTest {
    @Test
    void generalTest() {
        MessageType type = AND_REDUCE_MSG;
        assertThat(type, is(AND_REDUCE_MSG));
        assertThat(type.asChar(), is('A'));
        assertThat(type.asByte(), is((byte) 'A'));
        assertThat(type.toString(), is("AND_REDUCE_MSG"));
    }

}
