package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class MsgQueueTest {
    @SuppressWarnings( "StringBufferWithoutInitialCapacity" )
    @Test
    void runTest() {
        final StringBuilder output = new StringBuilder();
        final MsgQueue< String > msgQueue = new MsgQueue<>(msg -> {
            output.append(msg).append('\n');
        }, msg -> {
            return "end".equals(msg);
        });
        final Thread msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
        msgQueue.enqueue("hola");
        msgQueue.enqueue("como");
        msgQueue.enqueue("estas 123");
        msgQueue.enqueue("end");
        msgQueue.enqueue("lalalalal error");
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
