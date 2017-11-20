package ar.edu.unrc.pellegrini.franco.net;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ar.edu.unrc.pellegrini.franco.net.Message.MSG_TYPE_END;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings( { "ClassWithoutConstructor", "ClassIndependentOfModule" } )
class ServerTest {
    @Test
    void runTest() {
        try {
            final Queue< Message > receivedMessages = new ConcurrentLinkedQueue<>();
            final Server server = new Server(9001, msg -> {
                receivedMessages.add(msg);
                return !msg.isEndMessage();
            });
            final Thread serverThread = new Thread(server);
            serverThread.start();
            Thread.sleep(100L);
            final String      destAddress = "127.0.0.1";
            final InetAddress localHost   = InetAddress.getByName(destAddress);
            final Message     msg1        = new Message(localHost, 9001, "S:1000");
            Client.sendTo(server.getSocket(), msg1);
            final Message msg2 = new Message(localHost, 9001, "R:-9998881000");
            Client.sendTo(server.getSocket(), msg2);
            final Message msg3 = Message.newEndMessage();
            Client.sendTo(server.getSocket(), destAddress, 9001, MSG_TYPE_END);
            serverThread.join();
            if ( receivedMessages.isEmpty() ) { throw new AssertionError("server output is empty"); }
            assertThat(receivedMessages.containsAll(List.of(msg1, msg2, msg3)), is(true));
        } catch ( final Exception e ) {
            fail(e);
        }
    }

}
