package ar.edu.unrc.pellegrini.franco.net;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ar.edu.unrc.pellegrini.franco.net.Server.MSG_TYPE_END;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {
    @Test
    void run() {
        try {
            Queue< Message > receivedMessages = new ConcurrentLinkedQueue<>();
            Server server = new Server(9001, msg -> {
                receivedMessages.add(msg);
                return !msg.isEndMessage();
            });
            Thread serverThread = new Thread(server);
            serverThread.start();
            Thread.sleep(100);
            String      destAddress = "127.0.0.1";
            InetAddress localHost   = InetAddress.getByName(destAddress);
            Message     msg1        = new Message(localHost, 9001, "S:1000");
            Client.sendTo(server.getSocket(), msg1);
            Message msg2 = new Message(localHost, 9001, "R:-9998881000");
            Client.sendTo(server.getSocket(), msg2);
            Message msg3 = Message.newEndMessage();
            Client.sendTo(server.getSocket(), destAddress, 9001, MSG_TYPE_END);
            serverThread.join();
            if ( receivedMessages.isEmpty() ) { throw new AssertionError("server output is empty"); }
            assertThat(receivedMessages.containsAll(List.of(msg1, msg2, msg3)), is(true));
        } catch ( Exception e ) {
            fail(e);
        }
    }

}
