package ar.edu.unrc.pellegrini.franco.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class MsgQueueTest {
    @Test
    void run() {
        StringBuilder output = new StringBuilder();
        MsgQueue< String > msgQueue = new MsgQueue<>(msg -> {
            if ( msg.equals("end") ) {
                return false;
            } else {
                output.append(msg).append("\n");
                return true;
            }
        });
        Thread msgQueueThread = new Thread(msgQueue);
        msgQueueThread.start();
        msgQueue.enqueue("hola");
        msgQueue.enqueue("como");
        msgQueue.enqueue("estas 123");
        msgQueue.enqueue("end");
        msgQueue.enqueue("lalalalal error");
        try {
            Thread.sleep(10);
        } catch ( InterruptedException e ) {
            fail(e);
        }
        String finalMsg       = output.toString();
        String expectedOutput = "hola\ncomo\nestas 123\n";
        assertThat(finalMsg, is(expectedOutput));
        assertThat(msgQueueThread.isAlive(), is(false));
    }

}
