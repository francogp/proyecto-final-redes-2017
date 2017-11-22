package ar.edu.unrc.pellegrini.franco.net;

import org.junit.jupiter.api.Test;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.AND_REDUCE_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings( "ClassWithoutConstructor" )
class MessageTypeTest {
    @Test
    final
    void generalTest() {
        final MessageType type = AND_REDUCE_MSG;
        assertThat(type, is(AND_REDUCE_MSG));
        assertThat(type.asChar(), is('A'));
        assertThat(type.asByte(), is((byte) 'A'));
        assertThat(type.toString(), is("AND_REDUCE_MSG"));
    }

}
