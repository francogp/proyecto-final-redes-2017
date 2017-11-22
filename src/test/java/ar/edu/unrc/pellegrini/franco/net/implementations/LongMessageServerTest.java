package ar.edu.unrc.pellegrini.franco.net.implementations;

import ar.edu.unrc.pellegrini.franco.net.Message;
import ar.edu.unrc.pellegrini.franco.net.Server;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ar.edu.unrc.pellegrini.franco.net.MessageType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class LongMessageServerTest {
    @SuppressWarnings( "OverlyLongLambda" )
    @Test
    final
    void runTest() {
        try {
            final Queue< Message< Long > > receivedMessages = new ConcurrentLinkedQueue<>();
            final Server< Long > longMessageServer = new LongMessageServer(8001, receivedMessages::add, msg -> {
                if ( msg.isEndMessage() ) {
                    receivedMessages.add(msg);
                    return true;
                } else {
                    return false;
                }
            });
            final Thread serverThread = new Thread(longMessageServer);
            serverThread.start();
            Thread.sleep(100L);
            final String          destAddress = "127.0.0.1";
            final InetAddress     localHost   = InetAddress.getByName(destAddress);
            final Message< Long > msg1        = new LongMessage(localHost, 8001, READ_MSG, 1000L, 0L);
            longMessageServer.send(msg1);
            final Message< Long > msg2 = new LongMessage(localHost, 8001, WRITE_MSG, 9876L, -9998881000L);
            longMessageServer.send(msg2);
            final Message< Long > msg3 = new LongMessage(localHost, 8001, END_MSG, 0L, 0L);
            longMessageServer.send(msg3);
            serverThread.join();
            if ( receivedMessages.isEmpty() ) { throw new AssertionError("server output is empty"); }
            final List< Message< Long > > expected = List.of(msg1, msg2, msg3);
            assertThat(receivedMessages.containsAll(expected), is(true));
            assertThat(receivedMessages.size(), is(expected.size()));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
