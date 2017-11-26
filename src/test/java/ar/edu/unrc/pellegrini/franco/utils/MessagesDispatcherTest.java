package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class MessagesDispatcherTest {
    @SuppressWarnings( "StringBufferWithoutInitialCapacity" )
    @Test
    final
    void runTest() {
        final StringBuilder                output             = new StringBuilder();
        final MessagesDispatcher< String > messagesDispatcher = new MessagesDispatcher<>(msg -> output.append(msg).append('\n'), "end"::equals);
        final Thread                       msgQueueThread     = new Thread(messagesDispatcher);
        msgQueueThread.start();
        messagesDispatcher.enqueue("hola");
        messagesDispatcher.enqueue("como");
        messagesDispatcher.enqueue("estas 123");
        messagesDispatcher.enqueue("end");
        messagesDispatcher.enqueue("lalalalal error");
        try {
            Thread.sleep(10L);
        } catch ( final InterruptedException e ) {
            fail(e);
        }
        final String finalMsg       = output.toString();
        final String expectedOutput = "hola\ncomo\nestas 123\n";
        assertThat(finalMsg, is(expectedOutput));
        assertThat(msgQueueThread.isAlive(), is(false));
    }

}
