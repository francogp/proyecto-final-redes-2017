package ar.edu.unrc.pellegrini.franco.pgas.net;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.READ_MSG;
import static ar.edu.unrc.pellegrini.franco.pgas.net.MessageType.WRITE_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class ServerTest {
    @SuppressWarnings( "OverlyLongLambda" )
    @Test
    final
    void runTest() {
        try {
            final Queue< Message > receivedMessages = new ConcurrentLinkedQueue<>();
            final Server server = new Server(9001, receivedMessages::add, msg -> {
                if ( msg.isEndMessage() ) {
                    receivedMessages.add(msg);
                    return true;
                } else {
                    return false;
                }
            });
            final Thread serverThread = new Thread(server);
            serverThread.start();
            Thread.sleep(100L);
            final String      destAddress = "127.0.0.1";
            final InetAddress localHost   = InetAddress.getByName(destAddress);
            final Message     msg1        = new Message(localHost, 9001, READ_MSG, 1000L);
            server.send(msg1);
            final Message msg2 = new Message(localHost, 9001, WRITE_MSG, 9876L, -9998881000L);
            server.send(msg2);
            final Message msg3 = Message.defaultEndQueueMsg(localHost, 9001);
            server.send(msg3);
            serverThread.join();
            if ( receivedMessages.isEmpty() ) { throw new AssertionError("server output is empty"); }
            final List< Message > expected = List.of(msg1, msg2, msg3);
            assertThat(receivedMessages.containsAll(expected), is(true));
            assertThat(receivedMessages.size(), is(expected.size()));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
